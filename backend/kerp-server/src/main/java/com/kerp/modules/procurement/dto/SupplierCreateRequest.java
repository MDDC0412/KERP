package com.kerp.modules.procurement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierCreateRequest(
        @NotBlank(message = "Supplier code is required")
        @Size(max = 64, message = "Supplier code is too long")
        String code,
        @NotBlank(message = "Supplier name is required")
        @Size(max = 128, message = "Supplier name is too long")
        String name,
        @Size(max = 64, message = "Contact person is too long")
        String contactPerson,
        @Size(max = 32, message = "Contact phone is too long")
        String contactPhone,
        @Size(max = 128, message = "Payment terms is too long")
        String paymentTerms,
        @Size(max = 32, message = "Status is too long")
        String status
) {
}
