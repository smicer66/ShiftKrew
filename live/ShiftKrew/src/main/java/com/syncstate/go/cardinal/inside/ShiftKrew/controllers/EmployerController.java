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
@RequestMapping("/api/v1/services/employer")
public class EmployerController {


    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmployerService employerService;



    @RequestMapping(value="/post-a-casual-job", method = RequestMethod.POST)
    public ResponseEntity<AutoGraphResponse> postACasualJob(@RequestBody PostAJobRequest postAJobRequest) throws JsonProcessingException, AppException   //@RequestHeader(name = "Authorization") String token,
    {
        String jwtToken = this.request.getHeader("Authorization").substring("Bearer ".length());
        User user = tokenService.getUserFromToken(request);

        AutoGraphResponse autoGraphResponse = employerService.postACasualJob(user, postAJobRequest);
        return ResponseEntity.ok().body(autoGraphResponse);
    }

    @RequestMapping(value="/preview-invoice", method = RequestMethod.POST)
    public ResponseEntity<AutoGraphResponse> previewInvoice(@RequestBody PostAJobRequest postAJobRequest) throws JsonProcessingException, AppException   //@RequestHeader(name = "Authorization") String token,
    {
        String jwtToken = this.request.getHeader("Authorization").substring("Bearer ".length());
        User user = tokenService.getUserFromToken(request);

        AutoGraphResponse autoGraphResponse = employerService.previewInvoice(user, postAJobRequest);
        return ResponseEntity.ok().body(autoGraphResponse);
    }


    @RequestMapping(value="/cancel-a-casual-job", method = RequestMethod.POST)
    public ResponseEntity<AutoGraphResponse> cancelACasualJob(@RequestBody CancelAJobRequest cancelAJobRequest) throws JsonProcessingException, AppException   //@RequestHeader(name = "Authorization") String token,
    {
        String jwtToken = this.request.getHeader("Authorization").substring("Bearer ".length());
        User user = tokenService.getUserFromToken(request);

        AutoGraphResponse autoGraphResponse = employerService.cancelACasualJob(user, cancelAJobRequest);
        return ResponseEntity.ok().body(autoGraphResponse);
    }
}
