package com.syncstate.go.cardinal.inside.ShiftKrew.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.syncstate.go.cardinal.inside.ShiftKrew.exceptions.AppException;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.User;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.*;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.responses.AutoGraphResponse;
import com.syncstate.go.cardinal.inside.ShiftKrew.services.AuthService;
import com.syncstate.go.cardinal.inside.ShiftKrew.services.EmployerService;
import com.syncstate.go.cardinal.inside.ShiftKrew.services.TokenService;
import com.syncstate.go.cardinal.inside.ShiftKrew.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/services/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmployerService employerService;

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private HttpServletRequest request;

    @RequestMapping(value="/get-all-users")
    public ResponseEntity<AutoGraphResponse> getAllUsers()
    {
        AutoGraphResponse autoGraphResponse = userService.getAllUsers();
        return ResponseEntity.ok().body(autoGraphResponse);
    }

    @RequestMapping(value="/get-user-by-id/{userId}")
    public ResponseEntity<AutoGraphResponse> getUserById(@PathVariable Long userId)
    {
        AutoGraphResponse autoGraphResponse = userService.getUserById(userId);
        return ResponseEntity.ok().body(autoGraphResponse);
    }

    @RequestMapping(value="/create-new-user-account", method = RequestMethod.POST)
    public ResponseEntity<AutoGraphResponse> createNewUserAccount(@RequestBody CreateNewUserAccountRequest createNewUserAccountRequest)
    {
        AutoGraphResponse autoGraphResponse = userService.createNewUserAccount(createNewUserAccountRequest);
        return ResponseEntity.ok().body(autoGraphResponse);
    }

    @RequestMapping(value="/add-user-skillset", method = RequestMethod.POST)
    public ResponseEntity<AutoGraphResponse> updateUserSkillset(@RequestBody AddUserSkillRequest addUserSkillSetRequest) throws JsonProcessingException, AppException   //@RequestHeader(name = "Authorization") String token,
    {
        String jwtToken = this.request.getHeader("Authorization").substring("Bearer ".length());
        User user = tokenService.getUserFromToken(request);

        AutoGraphResponse autoGraphResponse = userService.addUserSkillset(user, addUserSkillSetRequest);
        return ResponseEntity.ok().body(autoGraphResponse);
    }

    @RequestMapping(value="/add-user-work-experience", method = RequestMethod.POST)
    public ResponseEntity<AutoGraphResponse> addUserWorkExperience(@RequestBody AddUserWorkExperienceRequest addUserWorkExperienceRequest) throws JsonProcessingException, AppException   //@RequestHeader(name = "Authorization") String token,
    {
        String jwtToken = this.request.getHeader("Authorization").substring("Bearer ".length());
        User user = tokenService.getUserFromToken(request);

        AutoGraphResponse autoGraphResponse = userService.addUserWorkExperience(user, addUserWorkExperienceRequest);
        return ResponseEntity.ok().body(autoGraphResponse);
    }

    @RequestMapping(value="/add-user-technical-training", method = RequestMethod.POST)
    public ResponseEntity<AutoGraphResponse> addUserTechnicalTraining(@RequestBody AddUserTechnicalTrainingRequest addUserTechnicalTrainingRequest) throws JsonProcessingException, AppException   //@RequestHeader(name = "Authorization") String token,
    {
        String jwtToken = this.request.getHeader("Authorization").substring("Bearer ".length());
        User user = tokenService.getUserFromToken(request);

        AutoGraphResponse autoGraphResponse = userService.addUserTechnicalTraining(user, addUserTechnicalTrainingRequest);
        return ResponseEntity.ok().body(autoGraphResponse);
    }


//    @RequestMapping(value="/login", method = RequestMethod.POST)
//    public ResponseEntity<AutoGraphResponse> loginCustomer(@RequestBody LoginRequest loginRequest)
//    {
//        ResponseEntity autoGraphResponse = authService.doLogin(loginRequest);
//        return autoGraphResponse;
//    }


    @RequestMapping(value="/get-user-data")
    public ResponseEntity<AutoGraphResponse> getUserData() throws JsonProcessingException {
        String jwtToken = this.request.getHeader("Authorization").substring("Bearer ".length());
        User user = tokenService.getUserFromToken(request);
        AutoGraphResponse autoGraphResponse = userService.getUserData(user);
        return ResponseEntity.ok().body(autoGraphResponse);
    }




    @RequestMapping(value="/add-business-to-user", method = RequestMethod.POST)
    public ResponseEntity<AutoGraphResponse> addBusinessToUser(@RequestBody AddBusinessToUserRequest addBusinessToUserRequest) throws JsonProcessingException, AppException   //@RequestHeader(name = "Authorization") String token,
    {
        String jwtToken = this.request.getHeader("Authorization").substring("Bearer ".length());
        User user = tokenService.getUserFromToken(request);

        AutoGraphResponse autoGraphResponse = employerService.addBusinessToUser(user, addBusinessToUserRequest);
        return ResponseEntity.ok().body(autoGraphResponse);
    }
}
