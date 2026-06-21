package com.syncstate.go.cardinal.inside.ShiftKrew.models.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserEmployerDTO {

    private Long userEmployerID;

    private String employerName;

    private String employerContactAddress;

    private String employerContactAddressPostCode;

    private String employerContactMobile;

    private String employerContactEmail;

    private String employerContactAddressCity;

    private String employerContactAddressState;

    private String employerContactAddressCountry;


    public UserEmployerDTO()
    {

    }

    public UserEmployerDTO(Long userEmployerID, String employerName, String employerContactAddress, String employerContactMobile,
                           String employerContactEmail, String employerContactAddressPostCode,
                           String employerContactAddressCity, String employerContactAddressState, String employerContactAddressCountry
           )
    {
        this.userEmployerID = userEmployerID;
        this.employerName = employerName;
        this.employerContactAddress = employerContactAddress;
        this.employerContactMobile = employerContactMobile;
        this.employerContactEmail = employerContactEmail;
        this.employerContactAddressPostCode = employerContactAddressPostCode;
        this.employerContactAddressCity = employerContactAddressCity;
        this.employerContactAddressState = employerContactAddressState;
        this.employerContactAddressCountry = employerContactAddressCountry;
    }
}
