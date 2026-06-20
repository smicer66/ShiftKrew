package com.syncstate.go.cardinal.inside.ShiftKrew.repositories;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.UserReferral;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.UserSkillSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSkillSetRepository extends JpaRepository<UserSkillSet, Long>{


}
