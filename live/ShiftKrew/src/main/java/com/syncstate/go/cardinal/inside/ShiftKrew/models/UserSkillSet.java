package com.syncstate.go.cardinal.inside.ShiftKrew.models;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.syncstate.go.cardinal.inside.ShiftKrew.deserializers.TimestampDeserializer;
import com.syncstate.go.cardinal.inside.ShiftKrew.serializers.JsonDateTimeSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "user_skillsets")
public class UserSkillSet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userSkillSetId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "skillSetId", nullable = false)
    private Long skillSetId;

    @Column(name = "isValid", nullable = false)
    private Boolean isValid;

    @Column(name = "skillSetExpertiseLevel", nullable = false)
    private int skillSetExpertiseLevel;



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
