package com.syncstate.go.cardinal.inside.ShiftKrew.models.requests;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StartAShiftRequest {

    @NotBlank(message="Provide the title of the casual job.")
    private Long shiftId;

    @NotBlank(message="Specify the employer posting this job.")
    private Long casualJobId;

    @NotBlank(message="Specify the employer posting this job.")
    private Long casualJobScheduleId;

    @NotBlank(message="Provide a reason for canceling this job.")
    private LocalDateTime localDateTime;

    private Double longitude;

    private Double latitude;
}
