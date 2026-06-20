package com.syncstate.go.cardinal.inside.ShiftKrew.repositories;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.User;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.UserReferral;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.dto.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserReferralRepository extends JpaRepository<UserReferral, Long>{


}
