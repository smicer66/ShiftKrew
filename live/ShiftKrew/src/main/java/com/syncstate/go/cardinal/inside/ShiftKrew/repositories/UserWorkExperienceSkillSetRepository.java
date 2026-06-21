package com.syncstate.go.cardinal.inside.ShiftKrew.repositories;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.UserWorkExperience;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.UserWorkExperienceSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWorkExperienceSkillSetRepository extends JpaRepository<UserWorkExperienceSkill, Long>{


}
