package com.syncstate.go.cardinal.inside.ShiftKrew.repositories;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.Invoice;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long>{


    @Query("SELECT u from InvoiceItem u WHERE u.casualJobId = :casualJobId")
    Collection<InvoiceItem> getByCasualJobId(Long casualJobId);
}
