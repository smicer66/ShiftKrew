package com.syncstate.go.cardinal.inside.ShiftKrew.models;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.syncstate.go.cardinal.inside.ShiftKrew.deserializers.TimestampDeserializer;
import com.syncstate.go.cardinal.inside.ShiftKrew.serializers.JsonDateTimeSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "invoices")
public class Invoice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    @Column(name = "userEmployerId", nullable = false)
    private Long userEmployerId;

    @Column(name = "casualJobId", nullable = false)
    private Long casualJobId;

    @Column(name = "invoiceNumber", nullable = false)
    private String invoiceNumber;

    @Column(name = "invoiceDescription", nullable = false)
    private String invoiceDescription;

    @Column(name = "casualJobDate", nullable = false)
    private LocalDate casualJobDate;

    @Column(name = "billToFullName", nullable = false)
    private String billToFullName;

    @Column(name = "billToLineAddress", nullable = false)
    private String billToLineAddress;

    @Column(name = "billToPostCodeAddress", nullable = false)
    private String billToPostCodeAddress;

    @Column(name = "billToAddressCity", nullable = false)
    private String billToAddressCity;

    @Column(name = "billToAddressState", nullable = false)
    private String billToAddressState;

    @Column(name = "billToAddressCountry", nullable = false)
    private String billToAddressCountry;

    @Column(name = "totalWages", nullable = false)
    private Double totalWages;

    @Column(name = "totalHolidayPay", nullable = false)
    private Double totalHolidayPay;

    @Column(name = "totalInsurancePension", nullable = false)
    private Double totalInsurancePension;

    @Column(name = "totalOtherAmount", nullable = false)
    private Double totalOtherAmount;

    @Column(name = "payToBankAccountId", nullable = false)
    private String payToBankAccountId;



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




    @PrePersist
    public void onCreate()
    {
        //Update these two fields any time we are creating an instance.
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

}
