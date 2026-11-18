package com.kerp.modules.attendance.controller;

import com.kerp.common.api.ApiResponse;
import com.kerp.common.security.RequirePermission;
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
import com.kerp.modules.attendance.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/shifts")
    public ApiResponse<List<Shift>> listShifts() {
        return ApiResponse.success(attendanceService.listShifts());
    }

    @PostMapping("/shifts")
    @RequirePermission("ATTENDANCE_MANAGE")
    public ApiResponse<Shift> createShift(@Valid @RequestBody ShiftCreateRequest request) {
        return ApiResponse.success(attendanceService.createShift(request));
    }

    @GetMapping("/groups")
    public ApiResponse<List<AttendanceGroup>> listGroups() {
        return ApiResponse.success(attendanceService.listGroups());
    }

    @PostMapping("/groups")
    @RequirePermission("ATTENDANCE_MANAGE")
    public ApiResponse<AttendanceGroup> createGroup(@Valid @RequestBody AttendanceGroupCreateRequest request) {
        return ApiResponse.success(attendanceService.createGroup(request));
    }

    @GetMapping("/schedules")
    public ApiResponse<List<Schedule>> listSchedules() {
        return ApiResponse.success(attendanceService.listSchedules());
    }

    @PostMapping("/schedules")
    @RequirePermission("ATTENDANCE_MANAGE")
    public ApiResponse<Schedule> createSchedule(@Valid @RequestBody ScheduleCreateRequest request) {
        return ApiResponse.success(attendanceService.createSchedule(request));
    }

    @GetMapping("/clock-records")
    public ApiResponse<List<ClockRecord>> listClockRecords() {
        return ApiResponse.success(attendanceService.listClockRecords());
    }

    @PostMapping("/clock-records")
    @RequirePermission("ATTENDANCE_MANAGE")
    public ApiResponse<ClockRecord> createClockRecord(@Valid @RequestBody ClockRecordCreateRequest request) {
        return ApiResponse.success(attendanceService.createClockRecord(request));
    }

    @GetMapping("/monthly-summaries")
    public ApiResponse<List<MonthlySummary>> listMonthlySummaries() {
        return ApiResponse.success(attendanceService.listMonthlySummaries());
    }

    @PostMapping("/monthly-summaries")
    @RequirePermission("ATTENDANCE_MANAGE")
    public ApiResponse<MonthlySummary> createMonthlySummary(@Valid @RequestBody MonthlySummaryCreateRequest request) {
        return ApiResponse.success(attendanceService.createMonthlySummary(request));
    }

    @PostMapping("/monthly-summaries/generate")
    @RequirePermission("ATTENDANCE_MANAGE")
    public ApiResponse<List<MonthlySummary>> generateMonthlySummaries(
            @Valid @RequestBody MonthlySummaryGenerateRequest request
    ) {
        return ApiResponse.success(attendanceService.generateMonthlySummaries(request));
    }
}
