package com.syncstate.go.cardinal.inside.ShiftKrew.models.requests;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EndAShiftRequest {

    @NotBlank(message="Provide the title of the casual job.")
    private Long shiftId;

    @NotBlank(message="Specify the employer posting this job.")
    private Long casualJobId;

    @NotBlank(message="Specify the employer posting this job.")
    private Long casualJobScheduleId;

    private Integer shiftRating;

    private String feedbackDetails;

    private Double longitude;

    private Double latitude;
}
