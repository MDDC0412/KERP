package com.kerp.modules.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kerp.common.exception.BusinessException;
import com.kerp.modules.approval.entity.ApprovalRequest;
import com.kerp.modules.approval.mapper.ApprovalRequestMapper;
import com.kerp.modules.attendance.dto.AttendanceGroupCreateRequest;
import com.kerp.modules.attendance.dto.ClockRecordCreateRequest;
import com.kerp.modules.attendance.dto.MonthlySummaryCreateRequest;
import com.kerp.modules.attendance.dto.MonthlySummaryGenerateRequest;
import com.kerp.modules.attendance.dto.ScheduleCreateRequest;
import com.kerp.modules.attendance.dto.ShiftCreateRequest;
import com.kerp.modules.attendance.entity.AttendanceGroup;
import com.kerp.modules.attendance.entity.ClockRecord;
import com.kerp.modules.attendance.entity.MonthlySummary;
import com.kerp.modules.attendance.entity.Schedule;
import com.kerp.modules.attendance.entity.Shift;
import com.kerp.modules.attendance.mapper.AttendanceGroupMapper;
import com.kerp.modules.attendance.mapper.ClockRecordMapper;
import com.kerp.modules.attendance.mapper.MonthlySummaryMapper;
import com.kerp.modules.attendance.mapper.ScheduleMapper;
import com.kerp.modules.attendance.mapper.ShiftMapper;
import com.kerp.modules.attendance.service.AttendanceService;
import com.kerp.modules.employee.entity.Employee;
import com.kerp.modules.employee.mapper.EmployeeMapper;
import com.kerp.modules.user.entity.User;
import com.kerp.modules.user.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final ShiftMapper shiftMapper;
    private final AttendanceGroupMapper attendanceGroupMapper;
    private final ScheduleMapper scheduleMapper;
    private final ClockRecordMapper clockRecordMapper;
    private final MonthlySummaryMapper monthlySummaryMapper;
    private final EmployeeMapper employeeMapper;
    private final UserMapper userMapper;
    private final ApprovalRequestMapper approvalRequestMapper;
    private final ObjectMapper objectMapper;

    public AttendanceServiceImpl(ShiftMapper shiftMapper,
                                 AttendanceGroupMapper attendanceGroupMapper,
                                 ScheduleMapper scheduleMapper,
                                 ClockRecordMapper clockRecordMapper,
                                 MonthlySummaryMapper monthlySummaryMapper,
                                 EmployeeMapper employeeMapper,
                                 UserMapper userMapper,
                                 ApprovalRequestMapper approvalRequestMapper,
                                 ObjectMapper objectMapper) {
        this.shiftMapper = shiftMapper;
        this.attendanceGroupMapper = attendanceGroupMapper;
        this.scheduleMapper = scheduleMapper;
        this.clockRecordMapper = clockRecordMapper;
        this.monthlySummaryMapper = monthlySummaryMapper;
        this.employeeMapper = employeeMapper;
        this.userMapper = userMapper;
        this.approvalRequestMapper = approvalRequestMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public Shift createShift(ShiftCreateRequest request) {
        long duplicateCount = shiftMapper.selectCount(
                new LambdaQueryWrapper<Shift>().eq(Shift::getCode, request.code())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("Shift code already exists");
        }
        Shift shift = new Shift();
        shift.setCode(request.code());
        shift.setName(request.name());
        shift.setStartTime(request.startTime());
        shift.setEndTime(request.endTime());
        shift.setRestMinutes(request.restMinutes() == null ? 60 : request.restMinutes());
        shiftMapper.insert(shift);
        return shift;
    }

    @Override
    public List<Shift> listShifts() {
        return shiftMapper.selectList(new LambdaQueryWrapper<Shift>().orderByAsc(Shift::getCode));
    }

    @Override
    public AttendanceGroup createGroup(AttendanceGroupCreateRequest request) {
        long duplicateCount = attendanceGroupMapper.selectCount(
                new LambdaQueryWrapper<AttendanceGroup>().eq(AttendanceGroup::getCode, request.code())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("Attendance group code already exists");
        }
        Shift shift = shiftMapper.selectById(request.shiftId());
        if (shift == null) {
            throw new BusinessException("Shift does not exist");
        }
        if (request.managerUserId() != null) {
            User manager = userMapper.selectById(request.managerUserId());
            if (manager == null) {
                throw new BusinessException("Manager user does not exist");
            }
        }
        AttendanceGroup attendanceGroup = new AttendanceGroup();
        attendanceGroup.setCode(request.code());
        attendanceGroup.setName(request.name());
        attendanceGroup.setShiftId(request.shiftId());
        attendanceGroup.setManagerUserId(request.managerUserId());
        attendanceGroupMapper.insert(attendanceGroup);
        return attendanceGroup;
    }

    @Override
    public List<AttendanceGroup> listGroups() {
        return attendanceGroupMapper.selectList(
                new LambdaQueryWrapper<AttendanceGroup>().orderByAsc(AttendanceGroup::getCode)
        );
    }

    @Override
    public Schedule createSchedule(ScheduleCreateRequest request) {
        assertEmployeeExists(request.employeeId());
        assertGroupExists(request.attendanceGroupId());
        assertShiftExists(request.shiftId());
        long duplicateCount = scheduleMapper.selectCount(
                new LambdaQueryWrapper<Schedule>()
                        .eq(Schedule::getEmployeeId, request.employeeId())
                        .eq(Schedule::getScheduleDate, request.scheduleDate())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("Schedule already exists for the employee on that date");
        }
        Schedule schedule = new Schedule();
        schedule.setEmployeeId(request.employeeId());
        schedule.setAttendanceGroupId(request.attendanceGroupId());
        schedule.setShiftId(request.shiftId());
        schedule.setScheduleDate(request.scheduleDate());
        scheduleMapper.insert(schedule);
        return schedule;
    }

    @Override
    public List<Schedule> listSchedules() {
        return scheduleMapper.selectList(new LambdaQueryWrapper<Schedule>().orderByDesc(Schedule::getScheduleDate));
    }

    @Override
    public ClockRecord createClockRecord(ClockRecordCreateRequest request) {
        assertEmployeeExists(request.employeeId());
        long duplicateCount = clockRecordMapper.selectCount(
                new LambdaQueryWrapper<ClockRecord>()
                        .eq(ClockRecord::getEmployeeId, request.employeeId())
                        .eq(ClockRecord::getClockDate, request.clockDate())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("Clock record already exists for the employee on that date");
        }
        ClockRecord clockRecord = new ClockRecord();
        clockRecord.setEmployeeId(request.employeeId());
        clockRecord.setClockDate(request.clockDate());
        clockRecord.setClockInTime(request.clockInTime());
        clockRecord.setClockOutTime(request.clockOutTime());
        clockRecord.setSource(request.source());
        clockRecord.setStatus(request.status());
        clockRecordMapper.insert(clockRecord);
        return clockRecord;
    }

    @Override
    public List<ClockRecord> listClockRecords() {
        return clockRecordMapper.selectList(new LambdaQueryWrapper<ClockRecord>().orderByDesc(ClockRecord::getClockDate));
    }

    @Override
    public MonthlySummary createMonthlySummary(MonthlySummaryCreateRequest request) {
        assertEmployeeExists(request.employeeId());
        long duplicateCount = monthlySummaryMapper.selectCount(
                new LambdaQueryWrapper<MonthlySummary>()
                        .eq(MonthlySummary::getEmployeeId, request.employeeId())
                        .eq(MonthlySummary::getSummaryMonth, request.summaryMonth())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("Monthly summary already exists");
        }
        MonthlySummary monthlySummary = new MonthlySummary();
        monthlySummary.setEmployeeId(request.employeeId());
        monthlySummary.setSummaryMonth(request.summaryMonth());
        monthlySummary.setShouldAttendDays(request.shouldAttendDays());
        monthlySummary.setActualAttendDays(request.actualAttendDays());
        monthlySummary.setLateCount(request.lateCount());
        monthlySummary.setLeaveHours(request.leaveHours());
        monthlySummary.setOvertimeHours(request.overtimeHours());
        monthlySummaryMapper.insert(monthlySummary);
        return monthlySummary;
    }

    @Override
    public List<MonthlySummary> listMonthlySummaries() {
        return monthlySummaryMapper.selectList(
                new LambdaQueryWrapper<MonthlySummary>().orderByDesc(MonthlySummary::getSummaryMonth)
        );
    }

    @Override
    @Transactional
    public List<MonthlySummary> generateMonthlySummaries(MonthlySummaryGenerateRequest request) {
        YearMonth yearMonth = YearMonth.parse(request.summaryMonth());
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Employee> employees = request.employeeId() == null
                ? employeeMapper.selectList(new LambdaQueryWrapper<Employee>().orderByAsc(Employee::getId))
                : List.of(employeeMapper.selectById(request.employeeId()));

        return employees.stream()
                .filter(item -> item != null)
                .map(item -> upsertMonthlySummary(item.getId(), request.summaryMonth(), startDate, endDate))
                .toList();
    }

    private MonthlySummary upsertMonthlySummary(Long employeeId,
                                                String summaryMonth,
                                                LocalDate startDate,
                                                LocalDate endDate) {
        long shouldAttendDays = scheduleMapper.selectCount(
                new LambdaQueryWrapper<Schedule>()
                        .eq(Schedule::getEmployeeId, employeeId)
                        .between(Schedule::getScheduleDate, startDate, endDate)
        );

        List<ClockRecord> clockRecords = clockRecordMapper.selectList(
                new LambdaQueryWrapper<ClockRecord>()
                        .eq(ClockRecord::getEmployeeId, employeeId)
                        .between(ClockRecord::getClockDate, startDate, endDate)
        );

        long actualAttendDays = clockRecords.stream()
                .filter(item -> item.getClockInTime() != null || item.getClockOutTime() != null)
                .count();
        long lateCount = clockRecords.stream()
                .filter(item -> "LATE".equalsIgnoreCase(item.getStatus()))
                .count();

        BigDecimal leaveHours = sumApprovalHours(employeeId, "LEAVE", summaryMonth);
        BigDecimal overtimeHours = sumApprovalHours(employeeId, "OVERTIME", summaryMonth);

        MonthlySummary monthlySummary = monthlySummaryMapper.selectOne(
                new LambdaQueryWrapper<MonthlySummary>()
                        .eq(MonthlySummary::getEmployeeId, employeeId)
                        .eq(MonthlySummary::getSummaryMonth, summaryMonth)
        );

        if (monthlySummary == null) {
            monthlySummary = new MonthlySummary();
            monthlySummary.setEmployeeId(employeeId);
            monthlySummary.setSummaryMonth(summaryMonth);
            monthlySummary.setShouldAttendDays(BigDecimal.valueOf(shouldAttendDays));
            monthlySummary.setActualAttendDays(BigDecimal.valueOf(actualAttendDays));
            monthlySummary.setLateCount((int) lateCount);
            monthlySummary.setLeaveHours(leaveHours);
            monthlySummary.setOvertimeHours(overtimeHours);
            monthlySummaryMapper.insert(monthlySummary);
        } else {
            monthlySummary.setShouldAttendDays(BigDecimal.valueOf(shouldAttendDays));
            monthlySummary.setActualAttendDays(BigDecimal.valueOf(actualAttendDays));
            monthlySummary.setLateCount((int) lateCount);
            monthlySummary.setLeaveHours(leaveHours);
            monthlySummary.setOvertimeHours(overtimeHours);
            monthlySummaryMapper.updateById(monthlySummary);
        }

        return monthlySummary;
    }

    private BigDecimal sumApprovalHours(Long employeeId, String approvalType, String summaryMonth) {
        List<ApprovalRequest> approvalRequests = approvalRequestMapper.selectList(
                new LambdaQueryWrapper<ApprovalRequest>()
                        .eq(ApprovalRequest::getEmployeeId, employeeId)
                        .eq(ApprovalRequest::getApprovalType, approvalType)
                        .eq(ApprovalRequest::getStatus, "APPROVED")
        );

        return approvalRequests.stream()
                .filter(item -> item.getSubmittedAt() != null)
                .filter(item -> summaryMonth.equals(item.getSubmittedAt().toLocalDate().toString().substring(0, 7)))
                .map(item -> extractHours(item.getFormDataJson()))
                .reduce(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), BigDecimal::add);
    }

    private BigDecimal extractHours(String formDataJson) {
        try {
            JsonNode rootNode = objectMapper.readTree(formDataJson);
            if (rootNode.hasNonNull("hours")) {
                return rootNode.get("hours").decimalValue().setScale(2, RoundingMode.HALF_UP);
            }
            if (rootNode.hasNonNull("days")) {
                return rootNode.get("days").decimalValue()
                        .multiply(BigDecimal.valueOf(8))
                        .setScale(2, RoundingMode.HALF_UP);
            }
        } catch (Exception ignored) {
        }
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    private void assertEmployeeExists(Long employeeId) {
        Employee employee = employeeMapper.selectById(employeeId);
        if (employee == null) {
            throw new BusinessException("Employee does not exist");
        }
    }

    private void assertGroupExists(Long groupId) {
        AttendanceGroup attendanceGroup = attendanceGroupMapper.selectById(groupId);
        if (attendanceGroup == null) {
            throw new BusinessException("Attendance group does not exist");
        }
    }

    private void assertShiftExists(Long shiftId) {
        Shift shift = shiftMapper.selectById(shiftId);
        if (shift == null) {
            throw new BusinessException("Shift does not exist");
        }
    }
}
