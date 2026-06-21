package com.syncstate.go.cardinal.inside.ShiftKrew.models.requests;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class AddUserWorkExperienceRequest {

    @NotNull(message="Select at least one skill related to this work experience")
    @Size(min=1, max=10, message="You must select at least one skill and a maximum of 10 skills.")
    private List<Long> userSkillList;

    @NotNull(message="Specify where you gained this experience")
    private String workLocation;

    @NotNull(message="Select at least one skill related to this work experience")
    private Date startDate;

    private Date endDate;

    @NotNull(message="Provide details on the work you did.")
    private String workExperienceDetails;

}
