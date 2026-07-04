package com.syncstate.go.cardinal.inside.ShiftKrew.repositories;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.CasualJob;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.CasualJobSchedule;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CasualJobScheduleRepository extends JpaRepository<CasualJobSchedule, Long>{

    @Query("SELECT u FROM CasualJobSchedule u WHERE u.jobId = :casualJobId")
    Collection<CasualJobSchedule> getCasualJobScheduleByCasualJobId(Long casualJobId);

    @Query("SELECT u FROM CasualJobSchedule u WHERE u.casualJobScheduleId IN :casualJobScheduleIdList")
    Collection<CasualJobSchedule> getCasualJobScheduleByCasualJobScheduleId(List<Long> casualJobScheduleIdList);

}
