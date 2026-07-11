package com.syncstate.go.cardinal.inside.ShiftKrew.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.syncstate.go.cardinal.inside.ShiftKrew.exceptions.AppException;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.User;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.*;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.responses.AutoGraphResponse;
import com.syncstate.go.cardinal.inside.ShiftKrew.services.AuthService;
import com.syncstate.go.cardinal.inside.ShiftKrew.services.TokenService;
import com.syncstate.go.cardinal.inside.ShiftKrew.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/services/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private HttpServletRequest request;



    @RequestMapping(value="/login", method = RequestMethod.POST)
    public ResponseEntity<AutoGraphResponse> loginCustomer(@RequestBody LoginRequest loginRequest)
    {
        ResponseEntity autoGraphResponse = authService.doLogin(loginRequest);
        return autoGraphResponse;
    }

}
