package com.syncstate.go.cardinal.inside.ShiftKrew.repositories;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{


    @Query("SELECT o from Transaction o WHERE o.deletedAt IS NULL AND o.organisationId = :organisationId")
    List<Transaction> getTransactionsByOrganisationId(Long organisationId);

}
