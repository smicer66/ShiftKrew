package com.syncstate.go.cardinal.inside.ShiftKrew.models;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.syncstate.go.cardinal.inside.ShiftKrew.deserializers.TimestampDeserializer;
import com.syncstate.go.cardinal.inside.ShiftKrew.enums.CasualJobStatus;
import com.syncstate.go.cardinal.inside.ShiftKrew.serializers.JsonDateTimeSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "casual_jobs")
public class CasualJob implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long casualJobId;

    @Column(name = "jobTitle", nullable = false)
    private String jobTitle;

    @Column(name = "jobDetails", nullable = false)
    private String jobDetails;

    @Column(name = "dressCode", nullable = false)
    private String dressCode;

    @Column(name = "jobLineAddress", nullable = false)
    private String jobLineAddress;

    @Column(name = "jobAddressPostCode", nullable = false)
    private String jobAddressPostCode;

    @Column(name = "contactPerson", nullable = true)
    private String contactPerson;

    @Column(name = "autoSelectFromFavorite", nullable = false)
    private Boolean autoSelectFromFavorite;

    @Column(name = "skillId", nullable = false)
    private Long skillId;

    @Column(name = "submittedByUserId", nullable = false)
    private Long submittedByUserId;

    @Column(name = "submittedByUserEmployerId", nullable = false)
    private Long submittedByUserEmployerId;

    @Column(name = "casualJobStatus", nullable = false)
    @Enumerated(EnumType.STRING)
    private CasualJobStatus casualJobStatus;

    @Column(name = "reasonForCancellation", nullable = true)
    private String reasonForCancellation;

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
