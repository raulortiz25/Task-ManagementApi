package com.TaskManagementApi.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtils {

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    @Value("${jwt.key.private}")
    private  String privateKey;

    @Value("${jwt.issuer}")
    private String userGenerator;

    public String createToken(Authentication authentication){

        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

        String username = authentication.getPrincipal().toString();

        String jwtToken = JWT.create()
                .withIssuer(this.userGenerator)
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + this.expirationMs))
                .withJWTId(UUID.randomUUID().toString())
                .sign(algorithm);

        return jwtToken;

    }

    public DecodedJWT validateToken (String token){

            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build();

            return verifier.verify(token);

    }

    public String extractUserName(DecodedJWT decodedJWT){
        return decodedJWT.getSubject().toString();
    }
}
