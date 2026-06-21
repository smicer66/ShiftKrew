package com.syncstate.go.cardinal.inside.ShiftKrew.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class UserDataDTO {

    private String username;

    private String firstName;

    private String lastName;

    private String status;

    private List<UserEmployerDTO> employerDataList;


    public UserDataDTO()
    {

    }

    public UserDataDTO(String username, String firstName, String lastName, String status)
    {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
    }
}
