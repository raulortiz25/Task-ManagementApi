package com.TaskManagementApi.Security;

import com.TaskManagementApi.DTO.AuthCreateUserRequest;
import com.TaskManagementApi.DTO.AuthLoginRequest;
import com.TaskManagementApi.DTO.AuthResponse;
import com.TaskManagementApi.Exception.Custom.ResourceAlreadyExistsException;
import com.TaskManagementApi.Model.UserEntity;
import com.TaskManagementApi.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(()->new BadCredentialsException("Usuario o contraseña invalida"));

        List<GrantedAuthority>authorityList = AuthorityUtils.createAuthorityList("ROLE_USER");

        return new User(user.getUsername(),
                user.getPassword(),
                authorityList);
    }

    public AuthResponse loginUser(AuthLoginRequest loginRequest){

        String username = loginRequest.username();
        String password = loginRequest.password();

        Authentication authentication = this.authenticate (username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accesToken = jwtUtils.createToken(authentication);

        AuthResponse authResponse = new AuthResponse(username, "User logged succesfusly", accesToken, true);

        return authResponse;
    }

    public Authentication authenticate (String username, String password){

        UserDetails userDetails = this.loadUserByUsername(username);

        if(!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new BadCredentialsException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());

    }

    public AuthResponse createUser (AuthCreateUserRequest createUserRequest){

        String username = createUserRequest.username();
        String password = createUserRequest.password();
        String email = createUserRequest.email();

        userRepository.findByUsername(username).ifPresent(user->{
            throw new ResourceAlreadyExistsException("El usuario con nombre: "+ username + " ya existe");
        });

        userRepository.findByEmail(email).ifPresent(user->{
            throw new ResourceAlreadyExistsException("El usuario con email: " + email + " ya existe");
        });


        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .isEnabled(true)
                .build();

        UserEntity userCreate = userRepository.save(userEntity);

        List<GrantedAuthority>authorityList = AuthorityUtils.createAuthorityList("ROLE_USER");

        Authentication authentication = new UsernamePasswordAuthenticationToken(userCreate.getUsername(), userCreate.getPassword(), authorityList);

        String accesToken = jwtUtils.createToken(authentication);

        AuthResponse authResponse = new AuthResponse(userCreate.getUsername(), "user created succesfusly", accesToken, true);

        return authResponse;
    }
}
