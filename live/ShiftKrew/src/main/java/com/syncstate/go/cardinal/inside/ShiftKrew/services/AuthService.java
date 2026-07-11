package com.syncstate.go.cardinal.inside.ShiftKrew.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.User;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.LoginRequest;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.responses.AuthResponse;
import com.syncstate.go.cardinal.inside.ShiftKrew.providers.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private TokenProvider jwtTokenUtil;
    @Autowired
    private AuthenticationManager authenticationManager;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public ResponseEntity<AuthResponse> doLogin(LoginRequest loginRequest) {
        try {
            //System.out.println(authenticationManager.);
            final Authentication authentication = authenticationManager.authenticate(
                    //                new AuthenticationManagerCustom(loginUser.getEmailAddress(), loginUser.getPassword())
                    //                new PayAccessAuthenticationProvider()

                    new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmailAddress(),
                        loginRequest.getPassword()
                    )
            );
            System.out.println(2);
            System.out.println(((User)authentication.getPrincipal()).getUserRole());


            logger.info("{}", authentication.isAuthenticated());
            logger.info("{}", authentication.getPrincipal());
            //        logger.info("{}>>>>", loginUser.getEmailAddress());



            SecurityContextHolder.getContext().setAuthentication(authentication);
            final String token = jwtTokenUtil.generateToken(authentication);
            logger.info("token...{}", token);


            AuthResponse loginResponse = new AuthResponse();
            loginResponse.setToken(token);
            loginResponse.setValid(true);//.setUsername(loginRequest.getUsername());
            loginResponse.setMessage("Login successful");


            return ResponseEntity.ok(loginResponse);
        }
        catch(ProviderNotFoundException | JsonProcessingException e)
        {
            System.out.println(77);
            AuthResponse loginResponse = new AuthResponse();
            loginResponse.setToken(null);
            loginResponse.setValid(true);//.setUsername(loginRequest.getUsername());
            loginResponse.setMessage("Invalid username/password combination. Please provide a valid username/password to log in");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
        }
    }

}
