package com.syncstate.go.cardinal.inside.ShiftKrew.repositories;


import com.syncstate.go.cardinal.inside.ShiftKrew.enums.WorkPermitStatus;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.UserTechnicalTraining;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.UserWorkPermit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkPermitRepository extends JpaRepository<UserWorkPermit, Long>{


    @Query("SELECT u from UserWorkPermit u WHERE u.workPermitStatus = :workPermitStatus AND " +
            "u.userId = :userId")
    UserWorkPermit getWorkPermitByUserIdAndStatus(Long userId, WorkPermitStatus workPermitStatus);


    @Query("SELECT u from UserWorkPermit u WHERE u.workPermitStatus = :workPermitStatus")
    UserWorkPermit getWorkPermitByStatus(WorkPermitStatus workPermitStatus);
}
