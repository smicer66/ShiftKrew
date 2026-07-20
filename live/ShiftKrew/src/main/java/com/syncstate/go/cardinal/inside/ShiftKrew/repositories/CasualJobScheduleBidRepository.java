package com.syncstate.go.cardinal.inside.ShiftKrew.repositories;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.CasualJobSchedule;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.CasualJobScheduleBid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public interface CasualJobScheduleBidRepository extends JpaRepository<CasualJobScheduleBid, Long>{

    @Query("SELECT u FROM CasualJobScheduleBid u WHERE u.bidId = :bidId" +
            "AND u.jobScheduleId IN :newList")
    List<CasualJobScheduleBid> getCasualJobScheduleBidByScheduleIdAndBidId(Long bidId, List newList);


    @Query("SELECT u FROM CasualJobScheduleBid u WHERE u.bidId = :bidId")
    List<CasualJobScheduleBid> getCasualJobScheduleBidByBidId(Long bidId);
}
