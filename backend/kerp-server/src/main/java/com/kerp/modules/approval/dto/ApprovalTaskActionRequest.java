package com.kerp.modules.approval.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ApprovalTaskActionRequest(
        @NotBlank(message = "Action is required")
        @Size(max = 16, message = "Action is too long")
        String action,
        @Size(max = 255, message = "Comment is too long")
        String comment
) {
}
