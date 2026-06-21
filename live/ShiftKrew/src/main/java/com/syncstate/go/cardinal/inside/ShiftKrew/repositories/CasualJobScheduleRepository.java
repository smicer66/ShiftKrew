package com.syncstate.go.cardinal.inside.ShiftKrew.repositories;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.CasualJob;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.CasualJobSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CasualJobScheduleRepository extends JpaRepository<CasualJobSchedule, Long>{


}
