package com.TaskManagementApi.Security;

import com.TaskManagementApi.Exception.DtoException.ErrorResponse;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class JwtTokenValidator extends OncePerRequestFilter {


    private JwtUtils jwtUtils;
    private ObjectMapper mapper;

    public JwtTokenValidator(JwtUtils jwtUtils, ObjectMapper mapper) {
        this.jwtUtils = jwtUtils;
        this.mapper= mapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);

            try {

                DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);

                String username = jwtUtils.extractUserName(decodedJWT);
                List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList("ROLE_USER");

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorityList);

                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);

            } catch (JWTVerificationException exception) {

                SecurityContextHolder.clearContext();
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                ErrorResponse error = new ErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        "TOKEN INVALIDO O EXPIRADO: " + exception.getMessage(),
                        request.getRequestURI()
                );

                response.getWriter().write(mapper.writeValueAsString(error));
                return;
            }

        }
        filterChain.doFilter(request, response);
    }
}
