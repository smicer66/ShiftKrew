package com.syncstate.go.cardinal.inside.ShiftKrew.repositories;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.EmployerTeam;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface EmployerTeamRepository extends JpaRepository<EmployerTeam, Long>{


    @Query("SELECT u from EmployerTeam u WHERE u.teamName = :teamName AND u.createdByUserId = :userId")
    EmployerTeam getEmployerTeamByTeamName(String teamName, Long userId);
}
