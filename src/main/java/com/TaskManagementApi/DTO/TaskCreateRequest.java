package com.TaskManagementApi.DTO;

import jakarta.validation.constraints.NotBlank;

public record TaskCreateRequest(@NotBlank(message = "El titulo es obligatorio") String title,
                                @NotBlank(message = "La descripcion de la tarea es obligatoria") String description) {
}
