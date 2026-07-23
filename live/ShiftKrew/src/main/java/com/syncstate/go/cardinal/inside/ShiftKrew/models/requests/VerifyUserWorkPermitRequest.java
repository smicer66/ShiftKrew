package com.syncstate.go.cardinal.inside.ShiftKrew.models.requests;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class VerifyUserWorkPermitRequest {

    @NotNull(message="Select the submitted visa you are verifying.")
    private Long userWorkPermitId;

    @NotNull(message="Specify the name of the visa holder.")
    private String visaHolderName;

    @NotNull(message="Specify the starting date of the visa")
    private LocalDate visaStartDAte;

    @NotNull(message="Specify the end date of the visa")
    private LocalDate visaEndDAte;

    @NotNull(message="Specify if you have successfully verified this work permit.")
    private String workPermitStatus;

}
