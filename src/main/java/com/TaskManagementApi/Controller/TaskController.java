package com.TaskManagementApi.Controller;

import com.TaskManagementApi.DTO.TaskCreateRequest;
import com.TaskManagementApi.DTO.TaskResponse;
import com.TaskManagementApi.DTO.TaskUpdateRequest;
import com.TaskManagementApi.Service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<Set<TaskResponse>> allTasks (){
        return new ResponseEntity<>(taskService.getAllUserTasks(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TaskResponse>createTask(@Valid @RequestBody TaskCreateRequest createRequest){
        return new ResponseEntity<>(taskService.createTask(createRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse>updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateRequest taskUpdateRequest){
        return new ResponseEntity<>(taskService.updateTask(id, taskUpdateRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
        return ResponseEntity.ok("La tarea ha sido eliminada correctamente");
    }


}
