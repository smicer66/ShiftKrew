package com.syncstate.go.cardinal.inside.ShiftKrew.models.requests;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RemoveTeamMemberRequest {

    @NotBlank(message="Specify the team you are removing a team member.")
    private Long teamId;

    @NotBlank(message="Specify the employee to be removed.")
    private Long userId;
}
