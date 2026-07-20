package com.syncstate.go.cardinal.inside.ShiftKrew.models.requests;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.dto.CasualJobScheduleDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateAJobRequest {

    @NotBlank(message="We can not find the casual job identifier.")
    private Long casualJobId;

    @NotBlank(message="Provide the title of the casual job.")
    private String jobTitle;

    @NotBlank(message="Specify the skill needed for this casual job.")
    private Long skillId;

    @NotBlank(message="Specify the skill needed for this casual job.")
    private String skillName;

    @NotBlank(message="Provide the schedule of the casual job.")
    private List<CasualJobScheduleDTO> jobSchedule;

    @NotBlank(message="Provide the description of the casual job.")
    private String jobDetails;

    @NotBlank(message="Provide the contact person who can be contacted during/ahead of the shift.")
    private String contactPerson;

    @NotBlank(message="Provide the dress code for this casual job.")
    private String dressCode;

    @NotBlank(message="Specify the employer posting this job.")
    private Long userEmployerId;

    @NotBlank(message="Provide the first line of address where this casual job is expected to happen.")
    private String jobLineAddress;

    @NotBlank(message="Provide the post code of the address where this casual job is expected to happen.")
    private String jobAddressPostCode;

    @NotBlank(message="Specify if an employee should be automatically selected from your favorites.")
    private Boolean autoSelectFromFavorite;

    @NotBlank(message="Specify if an employee should be automatically selected from your favorites.")
    private Integer autoPostThisForTheNextNthWeeks;
}
