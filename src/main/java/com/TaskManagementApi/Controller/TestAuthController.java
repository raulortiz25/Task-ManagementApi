package com.TaskManagementApi.Controller;

import com.TaskManagementApi.DTO.AuthCreateUserRequest;
import com.TaskManagementApi.DTO.AuthLoginRequest;
import com.TaskManagementApi.DTO.AuthResponse;
import com.TaskManagementApi.Security.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class TestAuthController {

    @Autowired
    private UserDetailsServiceImpl user;

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse>register(@RequestBody @Valid AuthCreateUserRequest createUserRequest){

        return new  ResponseEntity<>(this.user.createUser(createUserRequest),HttpStatus.CREATED);

    }

    @PostMapping("/log-in")
    public ResponseEntity<AuthResponse>login(@RequestBody @Valid AuthLoginRequest loginRequest){
        return new ResponseEntity<>(this.user.loginUser(loginRequest), HttpStatus.OK);
    }
}
