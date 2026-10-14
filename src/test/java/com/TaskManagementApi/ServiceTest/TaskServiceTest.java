package com.TaskManagementApi.ServiceTest;

import com.TaskManagementApi.DTO.TaskCreateRequest;
import com.TaskManagementApi.DTO.TaskResponse;
import com.TaskManagementApi.DTO.TaskUpdateRequest;
import com.TaskManagementApi.Exception.Custom.ResourceNotFoundException;
import com.TaskManagementApi.Exception.Custom.UnauthorizedAccessException;
import com.TaskManagementApi.Model.TaskEntity;
import com.TaskManagementApi.Model.UserEntity;
import com.TaskManagementApi.Repository.TaskRepository;
import com.TaskManagementApi.Repository.UserRepository;
import com.TaskManagementApi.Service.TaskService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito. *;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private TaskService taskService;

    @AfterEach
    void tearDown(){
        SecurityContextHolder.clearContext();
    }

    private void authenticateUser(String username){
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                username,
                null,
                List.of()
        );

        SecurityContextHolder .getContext().setAuthentication(authentication);
    }

    @Test
    public void getAllUserTasksTest(){

        authenticateUser("Josue");

        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("Josue")
                .build();

        TaskEntity task1 = TaskEntity.builder()
                .id(1L)
                .title("Estudiar Java")
                .description("Practicar Testing")
                .completed(false)
                .user(user)
                .build();


        TaskEntity task2 = TaskEntity.builder()
                .id(2L)
                .title("Estudiar Spring")
                .description("Aprender Mockito")
                .completed(false)
                .user(user)
                .build();


        Set<TaskEntity> tasks = Set.of(task1, task2);

        when(userRepo.findByUsername("Josue"))
                .thenReturn(Optional.of(user));

        when(taskRepo.findByUser_Username("Josue")).thenReturn(tasks);

        Set<TaskResponse>result = taskService.getAllUserTasks();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertTrue(
                result.stream()
                        .anyMatch(task ->task.title().equals("Estudiar Java"))
        );

        assertTrue(
                result.stream()
                        .anyMatch(task->task.title().equals("Estudiar Spring")));

        verify(userRepo).findByUsername("Josue");
        verify(taskRepo).findByUser_Username("Josue");



    }

    @Test
    public void getAllUserTasksUserNotFoundTest(){

        authenticateUser("Josue");

        when(userRepo.findByUsername("Josue")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, ()->taskService.getAllUserTasks());

        assertEquals("El usuario: Josue no existe", exception.getMessage());
        verify(userRepo).findByUsername("Josue");
        verify(taskRepo, never()).findByUser_Username(anyString());
    }

    @Test
    public void getAllUserTasksUserHasNoTasks(){
        authenticateUser("Josue");

        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("Josue")
                .build();

        Set<TaskEntity> tasks = Set.of();

        when(userRepo.findByUsername("Josue")).thenReturn(Optional.of(user));
        when(taskRepo.findByUser_Username("Josue")).thenReturn(tasks);

        Set<TaskResponse>result = taskService.getAllUserTasks();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepo).findByUsername("Josue");
        verify(taskRepo).findByUser_Username("Josue");
    }



    @Test
    public void createTaskTest(){

        authenticateUser("Josue");

        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("Josue")
                .build();

        TaskCreateRequest request = new TaskCreateRequest(
                "Estudiar Java",
                "Practicar JUnit y Mockito"
        );

        when(userRepo.findByUsername("Josue")).thenReturn(Optional.of(user));
        when(taskRepo.save(any(TaskEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponse result = taskService.createTask(request);

        ArgumentCaptor<TaskEntity> taskCaptor = ArgumentCaptor.forClass(TaskEntity.class);

        verify(taskRepo).save(taskCaptor.capture());
        TaskEntity savedTask = taskCaptor.getValue();

        assertEquals("Estudiar Java", savedTask.getTitle());
        assertEquals("Practicar JUnit y Mockito", savedTask.getDescription());
        assertFalse(savedTask.isCompleted());
        assertEquals(user, savedTask.getUser());

    }

    @Test
    public void shouldThrowExceptionWhenAuthenticatedUserDoesNotExist(){

        authenticateUser("Josue");

        TaskCreateRequest task1 = new TaskCreateRequest(
                "Aprender Spring",
                "Aprender JUnit"
        );

       when(userRepo.findByUsername("Josue")).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException .class, () ->taskService.createTask(task1));

        assertEquals("El usuario: Josue no existe", exception.getMessage());
        verify(userRepo).findByUsername("Josue");
        verify(taskRepo, never()).save(any(TaskEntity.class));

    }

    @Test
    public void updateTaskTest(){

        authenticateUser("Josue");

        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("Josue")
                .build();

        TaskEntity task = TaskEntity.builder()
                .id(1L)
                .title("Estudiar Java")
                .description("Estudiar JUnit")
                .completed(false)
                .user(user)
                .build();

        TaskUpdateRequest request = new TaskUpdateRequest( "Estudiar Spring", "Estudiar Mockito", true );

        when(userRepo.findByUsername("Josue")).thenReturn(Optional.of(user));
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepo.save(any(TaskEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponse result = taskService.updateTask(1L, request);

        ArgumentCaptor<TaskEntity> captor = ArgumentCaptor.forClass(TaskEntity.class);
        verify(taskRepo).save(captor.capture());
        TaskEntity updatededTask = captor.getValue();

        assertEquals("Estudiar Spring", updatededTask.getTitle());
        assertEquals("Estudiar Mockito", updatededTask.getDescription());
        assertTrue(updatededTask.isCompleted());

    }

    @Test
    public void shouldThrowExceptionWhenAuthenticatedUserDoesNotExistInUpdateTask(){

        authenticateUser("Josue");

        TaskUpdateRequest task = new TaskUpdateRequest(
                "Aprender Java",
                "Aprender condicionales",
                false
        );

        when(userRepo.findByUsername("Josue")).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, ()->taskService.updateTask(1L, task));

        assertEquals("El usuario: Josue no existe", exception.getMessage());

        verify(userRepo).findByUsername("Josue");
        verify(taskRepo, never()).findById(anyLong());
        verify(taskRepo, never()).save(any(TaskEntity.class));
    }

    @Test
    public void shouldThrowExceptionWhenTaskDoesNotExist(){

        authenticateUser("Josue");

        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("Josue")
                .build();

        TaskUpdateRequest task = new TaskUpdateRequest(
                "Estudiar Java",
                "Estudiar Mockito",
                false
        );

        when(userRepo.findByUsername("Josue")).thenReturn(Optional.of(user));
       ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, ()->taskService.updateTask(2L, task));

       assertEquals("la tarea con id: 2 no existe", exception.getMessage());

    }

    @Test
    public void shouldThrowExceptionWhenUserIsNotTaskOwner(){

        authenticateUser("Josue");
        authenticateUser("Roberto");

        UserEntity user1 = UserEntity.builder()
                .id(1L)
                .username("Josue")
                .build();

        UserEntity user2 = UserEntity.builder()
                .id(2L)
                .username("Roberto")
                .build();

        TaskEntity task = TaskEntity.builder()
                .id(1L)
                .title("Estudiar Java")
                .description("Estudiar JUnit")
                .completed(false)
                .user(user1)
                .build();

        TaskUpdateRequest request = new TaskUpdateRequest( "Estudiar Spring", "Estudiar Mockito", true );

        when(userRepo.findByUsername("Roberto")).thenReturn(Optional.of(user2));
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, ()->taskService.updateTask(1l, request));

        assertEquals("Acceso denegado: no tienes permiso para ver esta tarea", exception.getMessage());

        verify(taskRepo, never()).save(any(TaskEntity.class));
        verify(taskRepo).findById(1L);
    }

    @Test
    public void shouldThrowExceptionWhenAuthenticatedUserDoesNotExistInDeleteTask(){

        authenticateUser("Josue");

        when(userRepo.findByUsername("Josue")).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, ()->taskService.deleteTask(1L));

        assertEquals("El usuario: Josue no existe", exception.getMessage());

        verify(userRepo).findByUsername("Josue");
        verify(taskRepo, never()).findById(anyLong());
        verify(taskRepo, never()).delete(any(TaskEntity.class));
    }

    @Test
    public void shouldThrowExceptionWhenTaskDoesNotExistInDeleteTask(){

        authenticateUser("Josue");

        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("Josue")
                .build();

        when(userRepo.findByUsername("Josue")).thenReturn(Optional.of(user));
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, ()->taskService.deleteTask(1L));

        assertEquals("La tarea no existe", exception.getMessage());

        verify(taskRepo).findById(1L);
    }

    @Test
    public void shouldThrowExceptionWhenUserIsNotTaskOwnerInDeleteTask(){

        authenticateUser("Josue");
        authenticateUser("Roberto");

        UserEntity user1 = UserEntity.builder()
                .id(1L)
                .username("Josue")
                .build();

        UserEntity user2 = UserEntity.builder()
                .id(2L)
                .username("Roberto")
                .build();

        TaskEntity task = TaskEntity.builder()
                .id(1L)
                .title("Estudiar Java")
                .description("Estudiar JUnit")
                .completed(false)
                .user(user1)
                .build();

        when(userRepo.findByUsername("Roberto")).thenReturn(Optional.of(user2));
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, ()->taskService.deleteTask(1L));

        assertEquals("ACCESO DENEGADO: no puedes eliminar tareas que no son tuyas", exception.getMessage());

        verify(userRepo).findByUsername("Roberto");
        verify(taskRepo).findById(1L);
        verify(taskRepo, never()).delete(any(TaskEntity.class));
    }

    @Test
    public void deleteTaskTest(){
        authenticateUser("Josue");

        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("Josue")
                .build();

        TaskEntity task = TaskEntity.builder()
                .id(1L)
                .title("Estudiar Java")
                .description("Estudiar JUnit y Mockito")
                .completed(false)
                .user(user)
                .build();

        when(userRepo.findByUsername("Josue")).thenReturn(Optional.of(user));
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteTask(1L);

        verify(userRepo).findByUsername("Josue");
        verify(taskRepo).findById(1L);
        verify(taskRepo).delete(task);

    }

}
