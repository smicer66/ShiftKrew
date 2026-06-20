package com.syncstate.go.cardinal.inside.ShiftKrew.models.requests;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateNewUserAccountRequest {

    @NotBlank(message="Provide your first name.")
    @Size(min=2, max=30, message="The first name provided must be between 2 and 30 characters.")
    private String firstName;

    @NotBlank(message="Provide your last name.")
    @Size(min=2, max=30, message="The last name provided must be between 2 and 30 characters.")
    private String lastName;

    @NotBlank(message="Provide your email address.")
    @Size(min=2, max=50, message="Provide a valid email address.")
    private String emailAddress;

    @NotBlank(message="Provide your password.")
    @Size(min=8, message="Your password must be at least 8 characters long.")
    private String password;

    @Size(min=8, max=12, message="Provide a valid referral code.")
    private String referralCode;
}
