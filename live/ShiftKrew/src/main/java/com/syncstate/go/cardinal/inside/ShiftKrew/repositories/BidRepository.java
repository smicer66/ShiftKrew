package com.syncstate.go.cardinal.inside.ShiftKrew.repositories;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.Bid;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long>{


    @Query("SELECT u FROM Bid u WHERE u.casualJobId = :casualJobId AND u.bidSubmittedByUserId = :bidSubmittedByUserId")
    Bid getBidByCasualJobIdAndBidderId(Long casualJobId, Long bidSubmittedByUserId);

    @Query("SELECT u from Bid u WHERE u.casualJobId = :casualJobId AND u.bidId IN :bidIdList")
    Collection<Bid> getBidByBidIdsAndCasualJobId(List<Long> bidIdList, Long casualJobId);

    @Query("SELECT new com.syncstate.go.cardinal.inside.ShiftKrew.models.dto.BidDTO" +
            "(b.id, b.bidDetails, b.bidAmount, b.bidStatus, u.firstName + ' ' + u.lastName) from Bid b" +
            " INNER JOIN User u on u.userId = b.bidSubmittedByUserId" +
            " WHERE u.casualJobId = :casualJobId")
    Collection<Bid> getBidByCasualJobId(Long casualJobId);
}
