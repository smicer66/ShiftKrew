package com.syncstate.go.cardinal.inside.ShiftKrew.repositories;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.UserEmployer;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserEmployerRepository extends JpaRepository<UserEmployer, Long>{


    @Query("SELECT e FROM UserEmployer e WHERE e.userId = :userId AND e.employerName = :employerName")
    UserEmployer getUserEmployerByUserIdAndEmployerName(Long userId, String employerName);

    @Query("SELECT u FROM UserEmployer u WHERE u.userId  = :userId")
    List<UserEmployer> getUserEmployerByUserId(Long userId);

    @Query("SELECT u FROM UserEmployer u WHERE u.userEmployerId  = :userEmployerId")
    UserEmployer getUserEmployerByUserEmployerId(Long userEmployerId);
}
