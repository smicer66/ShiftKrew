package com.syncstate.go.cardinal.inside.ShiftKrew.models;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.syncstate.go.cardinal.inside.ShiftKrew.deserializers.TimestampDeserializer;
import com.syncstate.go.cardinal.inside.ShiftKrew.enums.TokenType;
import com.syncstate.go.cardinal.inside.ShiftKrew.serializers.JsonDateTimeSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
@Getter
@Setter
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "tokenType", nullable = false)
    private TokenType tokenType;

    @Column(name = "expiredAt", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "createdAt", nullable = false)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    @JsonDeserialize(using = TimestampDeserializer.class)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt", nullable = false)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    @JsonDeserialize(using = TimestampDeserializer.class)
    private LocalDateTime updatedAt;

    @Column(name = "deletedAt", nullable = true)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    @JsonDeserialize(using = TimestampDeserializer.class)
    private LocalDateTime deletedAt;

    @Column(name = "usedAt", nullable = true)
    private LocalDateTime usedAt;

    @Column(name = "tokenOwnedByEntityId", nullable = true)
    private BigInteger tokenOwnedByEntityId;

    @Lob
    @Column(name = "data", nullable = true)
    private String data;

    @PrePersist
    public void onCreate()
    {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
