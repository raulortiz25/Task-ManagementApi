package com.TaskManagementApi.DTO;

public record TaskResponse(Long id,
                           String title,
                           String description,
                           boolean Complete) {
}
