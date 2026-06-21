package com.syncstate.go.cardinal.inside.ShiftKrew.models.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserSkillDTO {

    private Long skillId;

    private int expertiseLevel;


    public UserSkillDTO()
    {

    }

    public UserSkillDTO(Long skillId, int expertiseLevel)
    {
        this.skillId = skillId;
        this.expertiseLevel = expertiseLevel;
    }
}
