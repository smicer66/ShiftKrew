package com.syncstate.go.cardinal.inside.ShiftKrew.repositories;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.TaskSchedule;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.UserReferral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskScheduleRepository extends JpaRepository<TaskSchedule, Long>{


    @Query("SELECT u FROM TaskSchedule u WHERE u.templateCasualJobId = :casualJobId")
    TaskSchedule getTaskScheduleByTemplateCasualJobId(Long casualJobId);
}
