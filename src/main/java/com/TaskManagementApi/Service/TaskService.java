package com.TaskManagementApi.Service;

import com.TaskManagementApi.DTO.TaskCreateRequest;
import com.TaskManagementApi.DTO.TaskResponse;
import com.TaskManagementApi.DTO.TaskUpdateRequest;
import com.TaskManagementApi.Exception.Custom.ResourceNotFoundException;
import com.TaskManagementApi.Exception.Custom.UnauthorizedAccessException;
import com.TaskManagementApi.Model.TaskEntity;
import com.TaskManagementApi.Model.UserEntity;
import com.TaskManagementApi.Repository.TaskRepository;
import com.TaskManagementApi.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    TaskRepository taskRepo;

    @Autowired
    UserRepository userRepo;

    private UserEntity getAuthenticatedUser (){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepo.findByUsername(username)
                .orElseThrow(()-> new ResourceNotFoundException("El usuario: " + username + " no existe"));

    }

    @Transactional
    public Set<TaskResponse>getAllUserTasks (){

        UserEntity userAuthenticate = getAuthenticatedUser();

        return taskRepo.findByUser_Username(userAuthenticate.getUsername())
                .stream()
                .map(task -> new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.isCompleted()))
                .collect(Collectors.toSet());
    }

    @Transactional
    public TaskResponse createTask (TaskCreateRequest createRequest){
        UserEntity userAuthenticate = getAuthenticatedUser();

        TaskEntity task = TaskEntity.builder()
                .title(createRequest.title())
                .description(createRequest.description())
                .completed(false)
                .user(userAuthenticate)
                .build();

        TaskEntity taskSave = taskRepo.save(task);

        return new TaskResponse(task.getId(), taskSave.getTitle(),
                taskSave.getDescription(), taskSave.isCompleted());
    }

    @Transactional
    public TaskResponse updateTask (Long taskId, TaskUpdateRequest updateRequest){

        UserEntity userAuthenticate = getAuthenticatedUser();

        TaskEntity task = taskRepo.findById(taskId)
                .orElseThrow(()-> new ResourceNotFoundException("la tarea con id: " + taskId + " no existe"));

        if(!task.getUser().getId().equals(userAuthenticate.getId())){
            throw new UnauthorizedAccessException("Acceso denegado: no tienes permiso para ver esta tarea");

        }

        task.setTitle(updateRequest.title());
        task.setDescription(updateRequest.description());
        task.setCompleted(updateRequest.Complete());

        TaskEntity saveUpdateTask = taskRepo.save(task);
        return  new TaskResponse(saveUpdateTask.getId(), saveUpdateTask.getTitle(),
                saveUpdateTask.getDescription(), saveUpdateTask.isCompleted());


    }

    @Transactional
    public void deleteTask(Long taskId){
        UserEntity userAuthenticate = getAuthenticatedUser();

        TaskEntity task = taskRepo.findById(taskId)
                .orElseThrow(()->new ResourceNotFoundException("La tarea no existe"));

        if(!task.getUser().getId().equals(userAuthenticate.getId())){
            throw new UnauthorizedAccessException("ACCESO DENEGADO: no puedes eliminar tareas que no son tuyas");
        }

        taskRepo.delete(task);
    }

}
