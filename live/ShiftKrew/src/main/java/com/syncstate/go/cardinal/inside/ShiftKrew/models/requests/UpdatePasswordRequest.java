package com.syncstate.go.cardinal.inside.ShiftKrew.models.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UpdatePasswordRequest {
    @NotBlank(message="Provide your new password.")
    private String password;

    @NotBlank(message="Confirm your new password.")
    private String confirmPassword;

}
