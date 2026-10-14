package com.TaskManagementApi.Repository;

import com.TaskManagementApi.Model.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    Set<TaskEntity> findByUser_Username(String username);
}
