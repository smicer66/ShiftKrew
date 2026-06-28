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
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "user_employers")
public class UserEmployer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userEmployerId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "employerName", nullable = false)
    private String employerName;

    @Column(name = "employerContactAddress", nullable = false)
    private String employerContactAddress;

    @Column(name = "employerContactAddressPostCode", nullable = false)
    private String employerContactAddressPostCode;

    @Column(name = "employerContactMobile", nullable = false)
    private String employerContactMobile;

    @Column(name = "employerContactEmail", nullable = false)
    private String employerContactEmail;

    @Column(name = "employerContactAddressCity", nullable = true)
    private String employerContactAddressCity;

    @Column(name = "employerContactAddressState", nullable = true)
    private String employerContactAddressState;

    @Column(name = "employerContactAddressCountry", nullable = true)
    private String employerContactAddressCountry;

    @Column(name = "isActive", nullable = true)
    private Boolean isActive;



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
