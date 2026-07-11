package com.syncstate.go.cardinal.inside.ShiftKrew.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.syncstate.go.cardinal.inside.ShiftKrew.exceptions.AppException;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.User;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.AddEmployerRequest;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.PostABidRequest;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.SelectWinningBidRequest;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.responses.AutoGraphResponse;
import com.syncstate.go.cardinal.inside.ShiftKrew.services.BidService;
import com.syncstate.go.cardinal.inside.ShiftKrew.services.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/services/bid")
public class BidController {


    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private BidService bidService;

    @RequestMapping(value="/post-a-bid", method = RequestMethod.POST)
    public ResponseEntity<AutoGraphResponse> postABid(@RequestBody PostABidRequest postABidRequest) throws JsonProcessingException, AppException   //@RequestHeader(name = "Authorization") String token,
    {
        String jwtToken = this.request.getHeader("Authorization").substring("Bearer ".length());
        User user = tokenService.getUserFromToken(request);

        AutoGraphResponse autoGraphResponse = bidService.postABid(user, postABidRequest);
        return ResponseEntity.ok().body(autoGraphResponse);
    }

    @RequestMapping(value="/cancel-bid/{bidId}", method = RequestMethod.GET)
    public ResponseEntity<AutoGraphResponse> cancelBid(@PathVariable Long bidId) throws JsonProcessingException, AppException   //@RequestHeader(name = "Authorization") String token,
    {
        String jwtToken = this.request.getHeader("Authorization").substring("Bearer ".length());
        User user = tokenService.getUserFromToken(request);



        AutoGraphResponse autoGraphResponse = bidService.cancelBid(user, bidId);
        return ResponseEntity.ok().body(autoGraphResponse);
    }

    @RequestMapping(value="/select-winning-bid", method = RequestMethod.POST)
    public ResponseEntity<AutoGraphResponse> selectWinningBid(@RequestBody SelectWinningBidRequest selectWinningBidRequest) throws JsonProcessingException, AppException   //@RequestHeader(name = "Authorization") String token,
    {
        String jwtToken = this.request.getHeader("Authorization").substring("Bearer ".length());
        User user = tokenService.getUserFromToken(request);



        AutoGraphResponse autoGraphResponse = bidService.selectWinningBid(user, selectWinningBidRequest);
        return ResponseEntity.ok().body(autoGraphResponse);
    }

}
