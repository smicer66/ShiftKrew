package com.syncstate.go.cardinal.inside.ShiftKrew.repositories;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.UserSkillSet;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.UserWorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWorkExperienceRepository extends JpaRepository<UserWorkExperience, Long>{


}
