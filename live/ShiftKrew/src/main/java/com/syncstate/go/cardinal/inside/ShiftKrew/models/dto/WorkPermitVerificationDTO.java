package com.syncstate.go.cardinal.inside.ShiftKrew.models.dto;

import com.syncstate.go.cardinal.inside.ShiftKrew.enums.WorkPermitStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDate;

public class WorkPermitVerificationDTO {
    private String workPermitNumber;

    private String visaHolderName;

    private LocalDate visaStartDAte;

    private LocalDate visaEndDAte;

    private String workPermitStatus;


    public WorkPermitVerificationDTO()
    {

    }

    public WorkPermitVerificationDTO(String workPermitNumber, String visaHolderName, LocalDate visaStartDAte, LocalDate visaEndDAte, String workPermitStatus) {
        this.workPermitNumber = workPermitNumber;
        this.visaHolderName = visaHolderName;
        this.visaStartDAte = visaStartDAte;
        this.visaEndDAte = visaEndDAte;
        this.workPermitStatus = workPermitStatus;
    }
}
