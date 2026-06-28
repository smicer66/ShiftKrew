package com.syncstate.go.cardinal.inside.ShiftKrew.models;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.syncstate.go.cardinal.inside.ShiftKrew.deserializers.TimestampDeserializer;
import com.syncstate.go.cardinal.inside.ShiftKrew.enums.BidStatus;
import com.syncstate.go.cardinal.inside.ShiftKrew.enums.CasualJobStatus;
import com.syncstate.go.cardinal.inside.ShiftKrew.enums.ShiftStatus;
import com.syncstate.go.cardinal.inside.ShiftKrew.serializers.JsonDateTimeSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "shifts")
public class Shift implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shiftId;

    @Column(name = "bidId", nullable = false)
    private Long bidId;

    @Column(name = "casualJobId", nullable = false)
    private Long casualJobId;

    @Column(name = "casualJobScheduleId", nullable = false)
    private Long casualJobScheduleId;

    @Column(name = "employeeUserId", nullable = false)
    private Long employeeUserId;

    @Column(name = "userEmployerId", nullable = false)
    private Long userEmployerId;

    @Column(name = "startTime", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "endTime", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "employeeSignedInAt", nullable = true)
    private LocalDateTime employeeSignedInAt;

    @Column(name = "employeeSignedOffAt", nullable = true)
    private LocalDateTime employeeSignedOffAt;

    @Column(name = "employerSignedInAt", nullable = true)
    private LocalDateTime employerSignedInAt;

    @Column(name = "employerSignedOffAt", nullable = true)
    private LocalDateTime employerSignedOffAt;

    @Column(name = "shiftStatus", nullable = false)
    @Enumerated(EnumType.STRING)
    private ShiftStatus shiftStatus;

    @Column(name = "reasonForCancellation", nullable = true)
    private String reasonForCancellation;

    @Column(name = "cancelledByUserId", nullable = true)
    private Long cancelledByUserId;

    @Column(name = "employeeLongitudeOnArrival", nullable = true)
    private Double employeeLongitudeOnArrival;

    @Column(name = "employeeLatitudeOnArrival", nullable = true)
    private Double employeeLatitudeOnArrival;

    @Column(name = "employeeLongitudeOnShiftEnd", nullable = true)
    private Double employeeLongitudeOnShiftEnd;

    @Column(name = "employeeLatitudeOnShiftEnd", nullable = true)
    private Double employeeLatitudeOnShiftEnd;

    @Column(name = "feedbackAboutEmployer", nullable = true)
    private String feedbackAboutEmployer;

    @Column(name = "feedbackAboutEmployee", nullable = true)
    private String feedbackAboutEmployee;

    @Column(name = "ratingByEmployee", nullable = true)
    private Integer ratingByEmployee;

    @Column(name = "ratingByEmployer", nullable = true)
    private Integer ratingByEmployer;

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
