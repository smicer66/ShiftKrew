package com.syncstate.go.cardinal.inside.ShiftKrew.models;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.syncstate.go.cardinal.inside.ShiftKrew.deserializers.TimestampDeserializer;
import com.syncstate.go.cardinal.inside.ShiftKrew.enums.UserStatus;
import com.syncstate.go.cardinal.inside.ShiftKrew.enums.WorkPermitStatus;
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
@Table(name = "user_work_permits")
public class UserWorkPermit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userWorkPermitId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "verifiedByUserId", nullable = true)
    private Long verifiedByUserId;

    @Column(name = "workPermitNumber", nullable = false)
    private String workPermitNumber;

    @Column(name = "visaHolderName", nullable = true)
    private String visaHolderName;

    @Column(name = "visaStartDAte", nullable = true)
    private LocalDate visaStartDAte;

    @Column(name = "visaEndDAte", nullable = true)
    private LocalDate visaEndDAte;

    @Column(name = "workPermitStatus", nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkPermitStatus workPermitStatus;

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
