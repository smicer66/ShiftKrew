package com.syncstate.go.cardinal.inside.ShiftKrew.repositories;


import com.syncstate.go.cardinal.inside.ShiftKrew.enums.InvoiceStatus;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.Invoice;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.Referral;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long>{

    @Query("SELECT u FROM Referral u WHERE u.referralCode = :referralCode AND u.usedByUserId IS NULL AND u.createdByUserId != userId")
    Referral getReferralByReferralCode(Long userId, String referralCode);
}
