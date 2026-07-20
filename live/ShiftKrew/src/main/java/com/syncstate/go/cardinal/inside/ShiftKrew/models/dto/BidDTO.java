package com.syncstate.go.cardinal.inside.ShiftKrew.models.dto;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.syncstate.go.cardinal.inside.ShiftKrew.deserializers.TimestampDeserializer;
import com.syncstate.go.cardinal.inside.ShiftKrew.enums.BidStatus;
import com.syncstate.go.cardinal.inside.ShiftKrew.serializers.JsonDateTimeSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
public class BidDTO {

    private Long bidId;

    private String bidDetails;

    private Double bidAmount;

    private BidStatus bidStatus;

    private String bidSubmittedByUser;


    public BidDTO()
    {

    }

    public BidDTO(Long bidId, String bidDetails, Double bidAmount, BidStatus bidStatus, String bidSubmittedByUser) {
        this.bidId = bidId;
        this.bidDetails = bidDetails;
        this.bidAmount = bidAmount;
        this.bidStatus = bidStatus;
        this.bidSubmittedByUser = bidSubmittedByUser;
    }
}
