package com.syncstate.go.cardinal.inside.ShiftKrew.models.requests;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.dto.BidScheduleWonDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SelectWinningBidRequest {


    @Size(min=1, message="Specify the winning bid")
    @NotNull(message="Specify the winning bid")
    private List<BidScheduleWonDTO> bidScheduleWonDTOList;

    @NotBlank(message="Specify the casual job.")
    private Long casualJobId;
}
