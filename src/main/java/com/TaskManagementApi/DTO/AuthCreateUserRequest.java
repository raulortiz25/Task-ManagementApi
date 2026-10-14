package com.TaskManagementApi.DTO;

import jakarta.validation.constraints.NotBlank;

public record AuthCreateUserRequest(@NotBlank String username,
                                    @NotBlank String email,
                                    @NotBlank String password) {
}
