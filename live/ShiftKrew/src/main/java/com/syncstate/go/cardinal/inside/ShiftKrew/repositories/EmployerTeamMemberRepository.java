package com.syncstate.go.cardinal.inside.ShiftKrew.repositories;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.EmployerTeam;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.EmployerTeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployerTeamMemberRepository extends JpaRepository<EmployerTeamMember, Long>{


}
