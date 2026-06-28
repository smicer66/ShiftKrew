package com.syncstate.go.cardinal.inside.ShiftKrew.repositories;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.Bid;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long>{


    @Query("SELECT u FROM Shift u WHERE u.employeeUserId = :userId ORDER BY u.startTime DESC")
    Collection<Shift> getAllShiftsByEmployeeUserId(Long userId);

    @Query("SELECT u FROM Shift u WHERE u.employeeUserId = :userId AND u.startTime > :startTime ORDER BY u.startTime DESC")
    Collection<Shift> getUpcomingShiftsByEmployeeUserId(Long userId, LocalDateTime startTime);

    @Query("SELECT u FROM Shift u WHERE u.casualJobId = :casualJobId AND u.casualJobScheduleId = :casualJobScheduleId AND u.shiftId = :shiftId")
    Shift getShiftByScheduleIdShiftIdAndJobId(Long casualJobScheduleId, Long shiftId, Long casualJobId);
}
