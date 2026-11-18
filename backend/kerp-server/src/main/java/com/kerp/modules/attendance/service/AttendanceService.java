package com.kerp.modules.attendance.service;

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

import java.util.List;

public interface AttendanceService {

    Shift createShift(ShiftCreateRequest request);

    List<Shift> listShifts();

    AttendanceGroup createGroup(AttendanceGroupCreateRequest request);

    List<AttendanceGroup> listGroups();

    Schedule createSchedule(ScheduleCreateRequest request);

    List<Schedule> listSchedules();

    ClockRecord createClockRecord(ClockRecordCreateRequest request);

    List<ClockRecord> listClockRecords();

    MonthlySummary createMonthlySummary(MonthlySummaryCreateRequest request);

    List<MonthlySummary> listMonthlySummaries();

    List<MonthlySummary> generateMonthlySummaries(MonthlySummaryGenerateRequest request);
}
