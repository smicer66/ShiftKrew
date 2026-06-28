package com.syncstate.go.cardinal.inside.ShiftKrew.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.syncstate.go.cardinal.inside.ShiftKrew.exceptions.AppException;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.User;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.CancelAShiftRequest;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.EndAShiftRequest;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.PostABidRequest;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.StartAShiftRequest;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.responses.AutoGraphResponse;
import com.syncstate.go.cardinal.inside.ShiftKrew.services.BidService;
import com.syncstate.go.cardinal.inside.ShiftKrew.services.ShiftService;
import com.syncstate.go.cardinal.inside.ShiftKrew.services.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/services/shifts")
public class ShiftController {


    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ShiftService shiftService;

    @RequestMapping(value="/cancel-a-shift", method = RequestMethod.POST)
    public ResponseEntity<AutoGraphResponse> cancelAShift(@RequestBody CancelAShiftRequest cancelAShiftRequest) throws JsonProcessingException, AppException   //@RequestHeader(name = "Authorization") String token,
    {
        String jwtToken = this.request.getHeader("Authorization").substring("Bearer ".length());
        User user = tokenService.getUserFromToken(request);

        AutoGraphResponse autoGraphResponse = shiftService.cancelAShift(user, cancelAShiftRequest);
        return ResponseEntity.ok().body(autoGraphResponse);
    }

    @RequestMapping(value="/get-all-shifts", method = RequestMethod.GET)
    public ResponseEntity<AutoGraphResponse> getAllShifts() throws JsonProcessingException, AppException   //@RequestHeader(name = "Authorization") String token,
    {
        String jwtToken = this.request.getHeader("Authorization").substring("Bearer ".length());
        User user = tokenService.getUserFromToken(request);

        AutoGraphResponse autoGraphResponse = shiftService.getAllEmployeeShifts(user);
        return ResponseEntity.ok().body(autoGraphResponse);
    }

    @RequestMapping(value="/get-upcoming-shifts", method = RequestMethod.GET)
    public ResponseEntity<AutoGraphResponse> getUpcomingShifts() throws JsonProcessingException, AppException   //@RequestHeader(name = "Authorization") String token,
    {
        String jwtToken = this.request.getHeader("Authorization").substring("Bearer ".length());
        User user = tokenService.getUserFromToken(request);

        AutoGraphResponse autoGraphResponse = shiftService.getUpcomingEmployeeShifts(user);
        return ResponseEntity.ok().body(autoGraphResponse);
    }

    @RequestMapping(value="/start-a-shift", method = RequestMethod.POST)
    public ResponseEntity<AutoGraphResponse> startAShift(@RequestBody StartAShiftRequest startAShiftRequest) throws JsonProcessingException, AppException   //@RequestHeader(name = "Authorization") String token,
    {
        String jwtToken = this.request.getHeader("Authorization").substring("Bearer ".length());
        User user = tokenService.getUserFromToken(request);

        AutoGraphResponse autoGraphResponse = shiftService.startAShift(user, startAShiftRequest);
        return ResponseEntity.ok().body(autoGraphResponse);
    }

    @RequestMapping(value="/end-a-shift", method = RequestMethod.POST)
    public ResponseEntity<AutoGraphResponse> endAShift(@RequestBody EndAShiftRequest endAShiftRequest) throws JsonProcessingException, AppException   //@RequestHeader(name = "Authorization") String token,
    {
        String jwtToken = this.request.getHeader("Authorization").substring("Bearer ".length());
        User user = tokenService.getUserFromToken(request);

        AutoGraphResponse autoGraphResponse = shiftService.endAShift(user, endAShiftRequest);
        return ResponseEntity.ok().body(autoGraphResponse);
    }


}
