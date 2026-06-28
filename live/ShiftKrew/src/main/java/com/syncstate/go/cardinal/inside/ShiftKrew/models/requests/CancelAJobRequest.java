package com.syncstate.go.cardinal.inside.ShiftKrew.models.requests;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.dto.CasualJobScheduleDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CancelAJobRequest {

    @NotBlank(message="Provide the title of the casual job.")
    private Long casualJobId;

    @NotBlank(message="Specify the employer posting this job.")
    private Long userEmployerId;

    @NotBlank(message="Provide a reason for canceling this job.")
    private String reasonForCancellation;
}
