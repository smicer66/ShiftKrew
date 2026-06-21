package com.syncstate.go.cardinal.inside.ShiftKrew.repositories;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.CasualJob;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CasualJobRepository extends JpaRepository<CasualJob, Long>{


}
