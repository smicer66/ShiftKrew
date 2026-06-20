package com.syncstate.go.cardinal.inside.ShiftKrew.models.requests;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.dto.UserSkillSetDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddUserSkillSetRequest {

    @NotNull(message="You must select at least one skill and a maximum of 10 skills.")
    @Size(min=1, max=10, message="You must select at least one skill and a maximum of 10 skills.")
    private List<UserSkillSetDTO> skillSetList;
}
