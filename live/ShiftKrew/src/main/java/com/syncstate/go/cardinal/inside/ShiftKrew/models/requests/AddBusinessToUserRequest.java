package com.syncstate.go.cardinal.inside.ShiftKrew.models.requests;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddBusinessToUserRequest {

    @NotBlank(message="Specify the address for your business/company.")
    private String employerContactAddress;

    @NotBlank(message="Provide your last name.")
    private String employerContactAddressPostCode;

    @NotBlank(message="Specify the email address for your business/company.")
    private String employerContactEmail;

    @NotBlank(message="Specify the mobile number for your business/company.")
    private String employerContactMobile;

    @NotBlank(message="Specify your business/company name  is situated in. This will be displayed to casual job employees when they apply for jobs you post.")
    private String employerName;

    @NotBlank(message="Specify the city your business is situated in. This will be where casual job employees will see as the location of casual jobs you post")
    private String employerContactAddressCity;

    @NotBlank(message="Specify the country your business is situated in. This will be where casual job employees will see as the location of casual jobs you post")
    private String employerContactAddressCountry;

    @NotBlank(message="Specify the county your business is situated in. This will be where casual job employees will see as the location of casual jobs you post.")
    private String employerContactAddressState;
}
