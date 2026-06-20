package com.syncstate.go.cardinal.inside.ShiftKrew.models.responses;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthResponse {
    private Boolean valid;
    private String message;
    private String token;


    public AuthResponse()
    {

    }
    public AuthResponse(Boolean valid, String message, String token)
    {
        this.valid = valid;
        this.message = message;
        this.token = token;
    }
}
