package com.syncstate.go.cardinal.inside.ShiftKrew.models.requests;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddNewTeamMemberRequest {

    @NotBlank(message="Specify at least one employee you have worked with previously.")
    private List<Long> userIdList;

    @NotBlank(message="Specify the team you are adding a member to.")
    private Long teamId;
}
