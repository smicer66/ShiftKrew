package com.syncstate.go.cardinal.inside.ShiftKrew.repositories;


import com.syncstate.go.cardinal.inside.ShiftKrew.enums.InvoiceStatus;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.CasualJob;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.Invoice;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>{


    @Query("SELECT u FROM Invoice u WHERE u.userEmployerId = :userEmployerId")
    List<Invoice> getInvoicesByEmployerId(Long userEmployerId);

    @Query("SELECT u FROM Invoice u WHERE u.isCredit = :b")
    Collection<Invoice> getInvoicesByCreditFlag(boolean b);

    @Query("SELECT u FROM Invoice u WHERE u.casualJobId = :casualJobId AND u.invoiceStatus = :invoiceStatus")
    Invoice getInvoiceByCasualJobIdAndStatus(Long casualJobId, InvoiceStatus invoiceStatus);

    @Query("SELECT u FROM Invoice u FROM u.casualJobId = :casualJobId")
    Collection<Invoice> getInvoiceByCasualJobId(Long casualJobId);
}
