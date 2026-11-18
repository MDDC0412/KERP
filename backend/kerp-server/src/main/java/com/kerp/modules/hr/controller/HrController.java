package com.kerp.modules.hr.controller;

import com.kerp.common.api.ApiResponse;
import com.kerp.modules.hr.dto.EmployeeChangeCreateRequest;
import com.kerp.modules.hr.dto.OffboardingCreateRequest;
import com.kerp.modules.hr.dto.OnboardingCreateRequest;
import com.kerp.modules.hr.entity.EmployeeChange;
import com.kerp.modules.hr.entity.OffboardingPlan;
import com.kerp.modules.hr.entity.OnboardingPlan;
import com.kerp.modules.hr.service.HrService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hr")
public class HrController {

    private final HrService hrService;

    public HrController(HrService hrService) {
        this.hrService = hrService;
    }

    @GetMapping("/changes")
    public ApiResponse<List<EmployeeChange>> listChanges() {
        return ApiResponse.success(hrService.listChanges());
    }

    @PostMapping("/changes")
    public ApiResponse<EmployeeChange> createChange(@Valid @RequestBody EmployeeChangeCreateRequest request) {
        return ApiResponse.success(hrService.createChange(request));
    }

    @GetMapping("/onboarding")
    public ApiResponse<List<OnboardingPlan>> listOnboardingPlans() {
        return ApiResponse.success(hrService.listOnboardingPlans());
    }

    @PostMapping("/onboarding")
    public ApiResponse<OnboardingPlan> createOnboarding(@Valid @RequestBody OnboardingCreateRequest request) {
        return ApiResponse.success(hrService.createOnboarding(request));
    }

    @GetMapping("/offboarding")
    public ApiResponse<List<OffboardingPlan>> listOffboardingPlans() {
        return ApiResponse.success(hrService.listOffboardingPlans());
    }

    @PostMapping("/offboarding")
    public ApiResponse<OffboardingPlan> createOffboarding(@Valid @RequestBody OffboardingCreateRequest request) {
        return ApiResponse.success(hrService.createOffboarding(request));
    }
}
