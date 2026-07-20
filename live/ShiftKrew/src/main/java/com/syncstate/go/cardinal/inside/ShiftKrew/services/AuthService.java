package com.syncstate.go.cardinal.inside.ShiftKrew.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.syncstate.go.cardinal.inside.ShiftKrew.enums.UserStatus;
import com.syncstate.go.cardinal.inside.ShiftKrew.exceptions.AppException;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.User;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.ForgotPasswordRequest;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.LoginRequest;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.UpdatePasswordRequest;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.responses.AuthResponse;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.responses.AutoGraphResponse;
import com.syncstate.go.cardinal.inside.ShiftKrew.providers.TokenProvider;
import org.apache.commons.lang3.RandomStringUtils;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private TokenProvider jwtTokenUtil;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

            User user = (User)authentication.getPrincipal();
            if(user.getUserStatus().equals(UserStatus.SIGNED_UP))
            {
                AuthResponse loginResponse = new AuthResponse();
                loginResponse.setToken(null);
                loginResponse.setValid(false);//.setUsername(loginRequest.getUsername());
                loginResponse.setMessage("Check your email for an email we sent to you containing a link to activate your account.");

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
            }

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

    public ResponseEntity forgotPassword(ForgotPasswordRequest forgotPasswordRequest) throws Exception {
        User user = this.userService.getUserByUsernameForLogin(forgotPasswordRequest.getEmailAddress());
        if(user==null)
        {
            throw new AppException("We have sent you an email with instructions on how to recover your password.");
        }

        String password = RandomStringUtils.randomAlphanumeric(12);
        user.setPassword(passwordEncoder.encode(password));
        this.userService.userRepository.save(user);

        AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
        autoGraphResponse.setStatus(0);
        autoGraphResponse.setStatusMessage("We have sent you an email with instructions on how to recover your password.");


        return ResponseEntity.ok(autoGraphResponse);
    }

    public ResponseEntity updatePassword(UpdatePasswordRequest updatePasswordRequest, User user) throws AppException {

        if(updatePasswordRequest.getConfirmPassword()!=null &&
            updatePasswordRequest.getPassword().equals(updatePasswordRequest.getConfirmPassword())
        )
        {
            user.setPassword(passwordEncoder.encode(updatePasswordRequest.getPassword()));
            this.userService.userRepository.save(user);

            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
            autoGraphResponse.setStatus(0);
            autoGraphResponse.setStatusMessage("Your password has been updated.");


            return ResponseEntity.ok(autoGraphResponse);
        }
        else
        {
            throw new AppException("Invalid password provided. The password provided and the confirmation password provided must match.");
        }
    }
}
