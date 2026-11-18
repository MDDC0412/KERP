package com.kerp.modules.hr.service;

import com.kerp.modules.hr.dto.EmployeeChangeCreateRequest;
import com.kerp.modules.hr.dto.OffboardingCreateRequest;
import com.kerp.modules.hr.dto.OnboardingCreateRequest;
import com.kerp.modules.hr.entity.EmployeeChange;
import com.kerp.modules.hr.entity.OffboardingPlan;
import com.kerp.modules.hr.entity.OnboardingPlan;

import java.util.List;

public interface HrService {

    EmployeeChange createChange(EmployeeChangeCreateRequest request);

    List<EmployeeChange> listChanges();

    OnboardingPlan createOnboarding(OnboardingCreateRequest request);

    List<OnboardingPlan> listOnboardingPlans();

    OffboardingPlan createOffboarding(OffboardingCreateRequest request);

    List<OffboardingPlan> listOffboardingPlans();
}
