package com.syncstate.go.cardinal.inside.ShiftKrew.models.dto;

import com.syncstate.go.cardinal.inside.ShiftKrew.models.CasualJobSchedule;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class CasualJobDTO {

    private String jobTitle;

    private String jobDetails;

    private String dressCode;

    private String jobLineAddress;

    private String jobAddressPostCode;

    private Boolean autoSelectFromFavorite;

    private String postByEmployer;

    private String employerJoinDate;

    private String contactPerson;

    private String skillRequired;

    private List<CasualJobScheduleDTO> casualJobSchedule;


    public CasualJobDTO()
    {

    }

    public CasualJobDTO(String jobTitle, String jobDetails, String dressCode, String jobLineAddress, String jobAddressPostCode, Boolean autoSelectFromFavorite, String postByEmployer, String employerJoinDate, String contactPerson) {
        this.jobTitle = jobTitle;
        this.jobDetails = jobDetails;
        this.dressCode = dressCode;
        this.jobLineAddress = jobLineAddress;
        this.jobAddressPostCode = jobAddressPostCode;
        this.autoSelectFromFavorite = autoSelectFromFavorite;
        this.postByEmployer = postByEmployer;
        this.employerJoinDate = employerJoinDate;
        this.contactPerson = contactPerson;
    }
}
