package com.TaskManagementApi.SecurityTest;

import com.TaskManagementApi.DTO.AuthCreateUserRequest;
import com.TaskManagementApi.DTO.AuthLoginRequest;
import com.TaskManagementApi.DTO.AuthResponse;
import com.TaskManagementApi.Model.UserEntity;
import com.TaskManagementApi.Repository.UserRepository;
import com.TaskManagementApi.Security.JwtUtils;
import com.TaskManagementApi.Security.UserDetailsServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito. *;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @AfterEach
    void tearDown(){
        SecurityContextHolder.clearContext();
    }

    @Test
    public void shouldLoadUserSuccessfully(){

        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("Josue")
                .password("12345")
                .build();

        when(userRepo.findByUsername("Josue")).thenReturn(Optional.of(user));

        UserDetails results = userDetailsService.loadUserByUsername("Josue");

        assertNotNull(results);
        assertEquals("Josue", results.getUsername());
        assertEquals("12345", results.getPassword());
        assertTrue(results.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));

        verify(userRepo).findByUsername("Josue");
    }

    @Test
    public void shouldThrowExceptionWhenUserDoesNotExist(){

        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("Josue")
                .password("12345")
                .build();

        when(userRepo.findByUsername("Carlos")).thenReturn(Optional.empty());

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                ()->userDetailsService.loadUserByUsername("Carlos"));

        assertEquals("Usuario o contraseña invalida", exception.getMessage());
        verify(userRepo).findByUsername("Carlos");


    }

    @Test
    public void shouldLoginSuccessfully(){

        AuthLoginRequest loginRequest = new AuthLoginRequest(
                "Josue",
                "12345"
        );

        UserEntity user = UserEntity.builder()
                .id(1l)
                .username("Josue")
                .password("contraseñaEncriptada")
                .email("josue234@gmail.com")
                .isEnabled(true)
                .build();

        when(userRepo.findByUsername("Josue")).thenReturn(Optional.of(user));

        when(passwordEncoder.matches("12345",
                "contraseñaEncriptada" )).thenReturn(true);

        when(jwtUtils.createToken(any(Authentication.class))).thenReturn("jwt-de-prueba");

        AuthResponse result = userDetailsService.loginUser(loginRequest);

        assertNotNull(result);
        assertEquals("Josue", result.username());
        assertEquals("User logged succesfusly", result.message());
        assertEquals("jwt-de-prueba",result.jwt() );
        assertTrue(result.status());

        assertEquals(
                "Josue",
                SecurityContextHolder.getContext().getAuthentication().getName()
        );

        verify(userRepo).findByUsername("Josue");
        verify(passwordEncoder).matches("12345", "contraseñaEncriptada");
        verify(jwtUtils).createToken(any(Authentication.class));

    }

    @Test
    public void shouldThrowExceptionWhenLoginUserDoesNotExist(){

        AuthLoginRequest request = new AuthLoginRequest(
                "Carlos",
                "12345"
        );

        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("Josue")
                .password("12345")
                .email("josue25@gmail.com")
                .isEnabled(true)
                .build();

        when(userRepo.findByUsername("Carlos")).thenReturn(Optional.empty());
        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                ()->userDetailsService.loginUser(request));

        assertEquals("Usuario o contraseña invalida", exception.getMessage());
        verify(userRepo).findByUsername("Carlos");
        verify(jwtUtils, never()).createToken(any(Authentication.class));
    }

    @Test
    public void shouldThrowExceptionWhenLoginPasswordIsIncorrect(){

        AuthLoginRequest request = new AuthLoginRequest(
                "Josue",
                "1234"
        );

        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("Josue")
                .password("contraseñaEncriptada")
                .email("josue25@gmail.com")
                .isEnabled(true)
                .build();

        when(userRepo.findByUsername("Josue")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("1234", "contraseñaEncriptada")).thenReturn(false);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                ()->userDetailsService.loginUser(request));

        assertEquals("Invalid password", exception.getMessage());
        verify(jwtUtils, never()).createToken(any(Authentication.class));

    }

    @Test
    public void shouldThrowExceptionWhenLoginUsernamePasswordIsEmpty(){

        AuthLoginRequest request = new AuthLoginRequest("", "");

        when(userRepo.findByUsername("")).thenReturn(Optional.empty());

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, ()->userDetailsService.loginUser(request));

        assertEquals("Usuario o contraseña invalida", exception.getMessage());

        verify(jwtUtils, never()).createToken(any(Authentication.class));

    }

    @Test
    public void shouldCreateUserSuccessfully(){

        AuthCreateUserRequest request = new AuthCreateUserRequest(
                "Josue",
                "josue25@gmail.com",
                "12345"

        );

        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("Josue")
                .email("josue25@gmail.com")
                .password("contraseñaEncriptada")
                .isEnabled(true)
                .build();

        when(userRepo.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoder.encode("12345"))
                .thenReturn("contraseñaEncriptada");

        when(jwtUtils.createToken(any(Authentication.class))).thenReturn("jwt-de-prueba");

        AuthResponse result = userDetailsService.createUser(request);

        ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);

        verify(jwtUtils).createToken(captor.capture());
        Authentication authentication = captor.getValue();

        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));

        assertNotNull(result);
        assertEquals("Josue", result.username());
        assertEquals("user created succesfusly",result.message());
        assertEquals("jwt-de-prueba",result.jwt() );

        verify(userRepo).findByUsername("Josue");
        verify(passwordEncoder).encode("12345");



    }




}
