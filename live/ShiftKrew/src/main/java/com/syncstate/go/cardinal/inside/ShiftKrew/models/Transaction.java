package com.syncstate.go.cardinal.inside.ShiftKrew.models;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.syncstate.go.cardinal.inside.ShiftKrew.deserializers.TimestampDeserializer;
import com.syncstate.go.cardinal.inside.ShiftKrew.enums.AutographCurrency;
import com.syncstate.go.cardinal.inside.ShiftKrew.enums.PaymentChannel;
import com.syncstate.go.cardinal.inside.ShiftKrew.enums.PaymentStatus;
import com.syncstate.go.cardinal.inside.ShiftKrew.serializers.JsonDateTimeSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "transactions")
public class Transaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @Column(name = "paidByUserId", nullable = false)
    private Long paidByUserId;

    @Column(name = "organisationId", nullable = true)
    private Long organisationId;

    @Column(nullable= false)
    @Enumerated(EnumType.STRING)
    private PaymentChannel paymentChannel;

    @Column(nullable= false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(nullable= false)
    @Enumerated(EnumType.STRING)
    private AutographCurrency autographCurrency;

    @Column(nullable= false)
    private String paymentRefNo;

    @Column(nullable= false)
    private String paymentProviderRefNo;

    @Column(nullable= false)
    private String narration;


    @JsonSerialize(using = JsonDateTimeSerializer.class)
    @JsonDeserialize(using = TimestampDeserializer.class)
    @Column(nullable= false)
    private LocalDateTime createdAt;

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    @JsonDeserialize(using = TimestampDeserializer.class)
    @Column(nullable= true)
    private LocalDateTime deletedAt;

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    @JsonDeserialize(using = TimestampDeserializer.class)
    @Column(nullable= false)
    private LocalDateTime updatedAt;

}
