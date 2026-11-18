package com.kerp.modules.attendance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kerp.common.model.BaseEntity;

import java.math.BigDecimal;

@TableName("att_month_summary")
public class MonthlySummary extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long employeeId;
    private String summaryMonth;
    private BigDecimal shouldAttendDays;
    private BigDecimal actualAttendDays;
    private Integer lateCount;
    private BigDecimal leaveHours;
    private BigDecimal overtimeHours;

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

    public String getSummaryMonth() {
        return summaryMonth;
    }

    public void setSummaryMonth(String summaryMonth) {
        this.summaryMonth = summaryMonth;
    }

    public BigDecimal getShouldAttendDays() {
        return shouldAttendDays;
    }

    public void setShouldAttendDays(BigDecimal shouldAttendDays) {
        this.shouldAttendDays = shouldAttendDays;
    }

    public BigDecimal getActualAttendDays() {
        return actualAttendDays;
    }

    public void setActualAttendDays(BigDecimal actualAttendDays) {
        this.actualAttendDays = actualAttendDays;
    }

    public Integer getLateCount() {
        return lateCount;
    }

    public void setLateCount(Integer lateCount) {
        this.lateCount = lateCount;
    }

    public BigDecimal getLeaveHours() {
        return leaveHours;
    }

    public void setLeaveHours(BigDecimal leaveHours) {
        this.leaveHours = leaveHours;
    }

    public BigDecimal getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(BigDecimal overtimeHours) {
        this.overtimeHours = overtimeHours;
    }
}
