package com.syncstate.go.cardinal.inside.ShiftKrew.models.requests;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.dto.UserSkillDTO;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.dto.UserTechnicalTrainingDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddUserTechnicalTrainingRequest {

    @NotNull(message="You must specify at least one training you have received.")
    @Size(min=1, max=10, message="You must specify at least one training you have received.")
    private List<UserTechnicalTrainingDTO> userTechnicalTrainingList;
}
