package com.kerp.modules.hr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kerp.common.exception.BusinessException;
import com.kerp.modules.employee.entity.Employee;
import com.kerp.modules.employee.mapper.EmployeeMapper;
import com.kerp.modules.hr.dto.EmployeeChangeCreateRequest;
import com.kerp.modules.hr.dto.OffboardingCreateRequest;
import com.kerp.modules.hr.dto.OnboardingCreateRequest;
import com.kerp.modules.hr.entity.EmployeeChange;
import com.kerp.modules.hr.entity.OffboardingPlan;
import com.kerp.modules.hr.entity.OnboardingPlan;
import com.kerp.modules.hr.mapper.EmployeeChangeMapper;
import com.kerp.modules.hr.mapper.OffboardingPlanMapper;
import com.kerp.modules.hr.mapper.OnboardingPlanMapper;
import com.kerp.modules.hr.service.HrService;
import com.kerp.modules.user.entity.User;
import com.kerp.modules.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HrServiceImpl implements HrService {

    private final EmployeeChangeMapper employeeChangeMapper;
    private final OnboardingPlanMapper onboardingPlanMapper;
    private final OffboardingPlanMapper offboardingPlanMapper;
    private final EmployeeMapper employeeMapper;
    private final UserMapper userMapper;

    public HrServiceImpl(EmployeeChangeMapper employeeChangeMapper,
                         OnboardingPlanMapper onboardingPlanMapper,
                         OffboardingPlanMapper offboardingPlanMapper,
                         EmployeeMapper employeeMapper,
                         UserMapper userMapper) {
        this.employeeChangeMapper = employeeChangeMapper;
        this.onboardingPlanMapper = onboardingPlanMapper;
        this.offboardingPlanMapper = offboardingPlanMapper;
        this.employeeMapper = employeeMapper;
        this.userMapper = userMapper;
    }

    @Override
    public EmployeeChange createChange(EmployeeChangeCreateRequest request) {
        assertEmployeeExists(request.employeeId());
        EmployeeChange employeeChange = new EmployeeChange();
        employeeChange.setEmployeeId(request.employeeId());
        employeeChange.setChangeType(request.changeType());
        employeeChange.setBeforeValue(request.beforeValue());
        employeeChange.setAfterValue(request.afterValue());
        employeeChange.setEffectiveDate(request.effectiveDate());
        employeeChange.setRemark(request.remark());
        employeeChangeMapper.insert(employeeChange);
        return employeeChange;
    }

    @Override
    public List<EmployeeChange> listChanges() {
        return employeeChangeMapper.selectList(new LambdaQueryWrapper<EmployeeChange>().orderByDesc(EmployeeChange::getId));
    }

    @Override
    public OnboardingPlan createOnboarding(OnboardingCreateRequest request) {
        assertEmployeeExists(request.employeeId());
        long duplicateCount = onboardingPlanMapper.selectCount(
                new LambdaQueryWrapper<OnboardingPlan>().eq(OnboardingPlan::getEmployeeId, request.employeeId())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("Onboarding plan already exists for the employee");
        }
        if (request.ownerUserId() != null) {
            User user = userMapper.selectById(request.ownerUserId());
            if (user == null) {
                throw new BusinessException("Owner user does not exist");
            }
        }
        OnboardingPlan onboardingPlan = new OnboardingPlan();
        onboardingPlan.setEmployeeId(request.employeeId());
        onboardingPlan.setPlanDate(request.planDate());
        onboardingPlan.setOwnerUserId(request.ownerUserId());
        onboardingPlan.setChecklistJson(request.checklistJson());
        onboardingPlan.setStatus(request.status());
        onboardingPlanMapper.insert(onboardingPlan);
        return onboardingPlan;
    }

    @Override
    public List<OnboardingPlan> listOnboardingPlans() {
        return onboardingPlanMapper.selectList(new LambdaQueryWrapper<OnboardingPlan>().orderByDesc(OnboardingPlan::getId));
    }

    @Override
    public OffboardingPlan createOffboarding(OffboardingCreateRequest request) {
        assertEmployeeExists(request.employeeId());
        long duplicateCount = offboardingPlanMapper.selectCount(
                new LambdaQueryWrapper<OffboardingPlan>().eq(OffboardingPlan::getEmployeeId, request.employeeId())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("Offboarding plan already exists for the employee");
        }
        OffboardingPlan offboardingPlan = new OffboardingPlan();
        offboardingPlan.setEmployeeId(request.employeeId());
        offboardingPlan.setPlanDate(request.planDate());
        offboardingPlan.setLastWorkingDate(request.lastWorkingDate());
        offboardingPlan.setReason(request.reason());
        offboardingPlan.setStatus(request.status());
        offboardingPlanMapper.insert(offboardingPlan);
        return offboardingPlan;
    }

    @Override
    public List<OffboardingPlan> listOffboardingPlans() {
        return offboardingPlanMapper.selectList(new LambdaQueryWrapper<OffboardingPlan>().orderByDesc(OffboardingPlan::getId));
    }

    private void assertEmployeeExists(Long employeeId) {
        Employee employee = employeeMapper.selectById(employeeId);
        if (employee == null) {
            throw new BusinessException("Employee does not exist");
        }
    }
}
