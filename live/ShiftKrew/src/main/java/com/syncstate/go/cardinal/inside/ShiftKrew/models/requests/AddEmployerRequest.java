package com.syncstate.go.cardinal.inside.ShiftKrew.models.requests;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.dto.UserSkillDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddEmployerRequest {

    @NotBlank(message="Provide the name of the employer.")
    private String employerName;


    @NotBlank(message="Provide the employers email address.")
    private String employerEmailAddress;

    @NotBlank(message="Provide the employers contact address.")
    private String employerContactAddress;

    @NotBlank(message="Provide the employers post code.")
    private String employerContactAddressPostCode;

    @NotBlank(message="Provide the employers contact mobile number.")
    private String employerContactMobile;

    @NotBlank(message="Provide the employers city.")
    private String employerContactAddressCity;

    @NotBlank(message="Provide the employers state/province.")
    private String employerContactAddressState;

    @NotBlank(message="Provide the employers country.")
    private String employerContactAddressCountry;
}
