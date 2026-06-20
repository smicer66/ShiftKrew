package com.syncstate.go.cardinal.inside.ShiftKrew.models.requests;


import com.syncstate.go.cardinal.inside.ShiftKrew.enums.TokenType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateTokenRequest {
    private String token;
    private String data;
    private String clientCode;
    private TokenType tokenType;

}
