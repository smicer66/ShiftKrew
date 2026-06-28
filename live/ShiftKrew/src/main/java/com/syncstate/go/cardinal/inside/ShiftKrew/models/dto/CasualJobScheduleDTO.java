package com.syncstate.go.cardinal.inside.ShiftKrew.models.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class CasualJobScheduleDTO {

    private LocalDateTime scheduleStartDate;

    private LocalDateTime scheduleEndDate;

    private int employeesNeeded;

    private Double payPerHour;

    private Double bonusPerHour;


    public CasualJobScheduleDTO()
    {

    }

    public CasualJobScheduleDTO(LocalDateTime scheduleStartDate, LocalDateTime scheduleEndDate, int employeesNeeded, Double payPerHour, Double bonusPerHour) {
        this.scheduleStartDate = scheduleStartDate;
        this.scheduleEndDate = scheduleEndDate;
        this.employeesNeeded = employeesNeeded;
        this.payPerHour = payPerHour;
        this.bonusPerHour = bonusPerHour;
    }
}
