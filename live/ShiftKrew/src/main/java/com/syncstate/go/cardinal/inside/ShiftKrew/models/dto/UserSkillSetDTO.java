package com.syncstate.go.cardinal.inside.ShiftKrew.models.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserSkillSetDTO {

    private Long skillSetId;

    private int expertiseLevel;


    public UserSkillSetDTO()
    {

    }

    public UserSkillSetDTO(Long skillSetId, int expertiseLevel)
    {
        this.skillSetId = skillSetId;
        this.expertiseLevel = expertiseLevel;
    }
}
