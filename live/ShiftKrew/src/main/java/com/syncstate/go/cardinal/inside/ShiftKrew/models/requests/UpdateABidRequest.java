package com.syncstate.go.cardinal.inside.ShiftKrew.models.requests;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UpdateABidRequest {

    @NotBlank(message="Specify the bid you are updating,")
    private Long bidId;

    @NotBlank(message="A casual job must be selected to submit a bid.")
    private Long casualJobId;

    @NotBlank(message="Specify the details of your bid.")
    private String bidDetails;

    @NotBlank(message="Specify how much you want to be paid per hour.")
    private Double bidAmountPerHour;

    @NotBlank(message="Specify the latest date you want to be notified of being assigned the job.")
    private LocalDateTime bidResponseDeadline;

    @Size(min=1, message="Specify the schedule this bids are for.")
    @NotNull(message="pecify the schedule this bids are for.")
    private List<Long> bidScheduleIdList;
}