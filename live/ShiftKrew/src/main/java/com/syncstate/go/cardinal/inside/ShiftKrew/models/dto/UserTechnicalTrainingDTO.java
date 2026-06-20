package com.syncstate.go.cardinal.inside.ShiftKrew.models.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class UserTechnicalTrainingDTO {

    private Long userTechnicalTrainingID;

    private String technicalTrainingDetails;

    private LocalDate dateObtained;

    private LocalDate dateExpires;

    public UserTechnicalTrainingDTO()
    {

    }

    public UserTechnicalTrainingDTO(Long userTechnicalTrainingID, String technicalTrainingDetails)
    {
        this.userTechnicalTrainingID = userTechnicalTrainingID;
        this.technicalTrainingDetails = technicalTrainingDetails;
    }
}
