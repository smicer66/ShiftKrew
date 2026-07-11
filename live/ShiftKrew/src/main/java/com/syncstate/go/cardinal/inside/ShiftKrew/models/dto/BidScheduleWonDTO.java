package com.syncstate.go.cardinal.inside.ShiftKrew.models.dto;


import com.syncstate.go.cardinal.inside.ShiftKrew.enums.BidStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class BidScheduleWonDTO {

    @NotNull(message="Specify the casual job.")
    private Long casualJobScheduleId;


    @Size(min=1, message="Specify the winning bid for each job selected")
    private List<Long> bidIdList;
}
