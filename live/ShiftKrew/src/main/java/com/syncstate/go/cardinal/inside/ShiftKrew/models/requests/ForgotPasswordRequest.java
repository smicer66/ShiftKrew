package com.syncstate.go.cardinal.inside.ShiftKrew.models.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ForgotPasswordRequest {
    @NotBlank(message="Provide your email address.")
    private String emailAddress;


}
