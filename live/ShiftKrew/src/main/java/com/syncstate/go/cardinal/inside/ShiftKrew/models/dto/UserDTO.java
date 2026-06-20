package com.syncstate.go.cardinal.inside.ShiftKrew.models.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserDTO {

    private String username;

    private String firstName;

    private String lastName;


    public UserDTO()
    {

    }

    public UserDTO(String username, String firstName, String lastName)
    {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
