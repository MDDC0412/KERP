package com.kerp.modules.attendance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kerp.common.model.BaseEntity;

import java.time.LocalDate;

@TableName("att_schedule")
public class Schedule extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long employeeId;
    private Long attendanceGroupId;
    private Long shiftId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduleDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getAttendanceGroupId() {
        return attendanceGroupId;
    }

    public void setAttendanceGroupId(Long attendanceGroupId) {
        this.attendanceGroupId = attendanceGroupId;
    }

    public Long getShiftId() {
        return shiftId;
    }

    public void setShiftId(Long shiftId) {
        this.shiftId = shiftId;
    }

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }
}
