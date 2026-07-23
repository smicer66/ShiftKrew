package com.syncstate.go.cardinal.inside.ShiftKrew.models.requests;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.dto.UserTechnicalTrainingDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddWorkPermitRequest {

    @NotNull(message="You must specify a valid work permit.")
    @Size(min=1, max=20, message="You must specify a valid work permit.")
    private String workPermitNumber;
}
