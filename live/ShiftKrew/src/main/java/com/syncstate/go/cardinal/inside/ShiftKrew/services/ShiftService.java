package com.syncstate.go.cardinal.inside.ShiftKrew.services;


import com.syncstate.go.cardinal.inside.ShiftKrew.enums.Role;
import com.syncstate.go.cardinal.inside.ShiftKrew.enums.ShiftStatus;
import com.syncstate.go.cardinal.inside.ShiftKrew.exceptions.AppException;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.*;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.*;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.responses.AutoGraphResponse;
import com.syncstate.go.cardinal.inside.ShiftKrew.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

@Service
public class ShiftService {

    @Autowired
    ShiftRepository shiftRepository;

    @Autowired
    CasualJobRepository casualJobRepository;

    public AutoGraphResponse cancelAShift(User user, CancelAShiftRequest cancelAShiftRequest) throws AppException {
        Shift shift = this.shiftRepository.getShiftByScheduleIdShiftIdAndJobId(cancelAShiftRequest.getCasualJobScheduleId(),
                cancelAShiftRequest.getShiftId(), cancelAShiftRequest.getCasualJobId());
        CasualJob casualJob = this.casualJobRepository.getById(shift.getCasualJobId());
        if(user.getUserRole().equals(Role.EMPLOYER) && casualJob.getSubmittedByUserId().equals(user.getUserId()))
        {
            shift.setShiftStatus(ShiftStatus.CANCELLED);
            shift.setReasonForCancellation(cancelAShiftRequest.getShiftCancellationReason());
            shift.setCancelledByUserId(user.getUserId());
            this.shiftRepository.save(shift);

            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
            autoGraphResponse.setStatus(0);
            autoGraphResponse.setStatusMessage("This shift has been cancelled. We will contact you to get more details about this cancellation.");

            return autoGraphResponse;
        }
        else if(user.getUserRole().equals(Role.EMPLOYEE) && shift.getEmployeeUserId().equals(user.getUserId()))
        {
            shift.setShiftStatus(ShiftStatus.CANCELLED);
            shift.setReasonForCancellation(cancelAShiftRequest.getShiftCancellationReason());
            shift.setCancelledByUserId(user.getUserId());
            this.shiftRepository.save(shift);

            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
            autoGraphResponse.setStatus(0);
            autoGraphResponse.setStatusMessage("This shift has been cancelled. We will contact you to get more details about this cancellation.");

            return autoGraphResponse;
        }

        throw new AppException("You can not cancel this shift. You need to be the owner of the job or the employee assigned to the job.");
    }




    public AutoGraphResponse getAllEmployeeShifts(User user)
    {
        Collection<Shift> shiftList = this.shiftRepository.getAllShiftsByEmployeeUserId(user.getUserId());

        if(shiftList!=null && shiftList.size()>0)
        {
            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
            autoGraphResponse.setStatus(0);
            autoGraphResponse.setStatusMessage("Employee shifts found.");
            autoGraphResponse.setResponseData(shiftList);

            return autoGraphResponse;
        }

        AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
        autoGraphResponse.setStatus(0);
        autoGraphResponse.setStatusMessage("No employee shifts are available for you.");
        autoGraphResponse.setResponseData(shiftList);

        return autoGraphResponse;

    }


    public AutoGraphResponse getUpcomingEmployeeShifts(User user)
    {
        LocalDateTime startTime = LocalDateTime.of(
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                LocalDate.now().getDayOfMonth(),
                0, 0, 0
        );
        Collection<Shift> shiftList = this.shiftRepository.getUpcomingShiftsByEmployeeUserId(user.getUserId(), startTime);

        if(shiftList!=null && shiftList.size()>0)
        {
            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
            autoGraphResponse.setStatus(0);
            autoGraphResponse.setStatusMessage("Employee shifts found.");
            autoGraphResponse.setResponseData(shiftList);

            return autoGraphResponse;
        }

        AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
        autoGraphResponse.setStatus(0);
        autoGraphResponse.setStatusMessage("No employee shifts are available for you.");
        autoGraphResponse.setResponseData(shiftList);

        return autoGraphResponse;

    }



    public AutoGraphResponse startAShift(User user, StartAShiftRequest startAShiftRequest) throws AppException {
        Shift shift = this.shiftRepository.getShiftByScheduleIdShiftIdAndJobId(
                startAShiftRequest.getCasualJobScheduleId(),
                startAShiftRequest.getShiftId(),
                startAShiftRequest.getCasualJobId());
        CasualJob casualJob = this.casualJobRepository.getById(shift.getCasualJobId());

        if(
                Arrays.asList(
                        new String[]{ShiftStatus.PENDING.name()}
                ).contains(shift.getShiftStatus().name()) &&
                        user.getUserRole().equals(Role.EMPLOYEE) &&
                        shift.getEmployeeUserId().equals(user.getUserId()))
        {
            shift.setShiftStatus(ShiftStatus.EMPLOYEE_ARRIVED_FOR_SHIFT);
            shift.setEmployeeSignedInAt(LocalDateTime.now());
            shift.setEmployeeLongitudeOnArrival(startAShiftRequest.getLongitude());
            shift.setEmployeeLatitudeOnArrival(startAShiftRequest.getLatitude());
            this.shiftRepository.save(shift);

            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
            autoGraphResponse.setStatus(0);
            autoGraphResponse.setStatusMessage("Thanks for informing us that you have arrived for your shift.");

            return autoGraphResponse;
        }
        else if(
                Arrays.asList(
                        new String[]{ShiftStatus.EMPLOYEE_ARRIVED_FOR_SHIFT.name(), ShiftStatus.PENDING.name()}
                ).contains(shift.getShiftStatus().name()) &&
                user.getUserRole().equals(Role.EMPLOYER) &&
                casualJob.getSubmittedByUserId().equals(user.getUserId()))
        {
            shift.setShiftStatus(ShiftStatus.EMPLOYER_STARTED_SHIFT);
            shift.setEmployerSignedInAt(LocalDateTime.now());
            this.shiftRepository.save(shift);

            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
            autoGraphResponse.setStatus(0);
            autoGraphResponse.setStatusMessage("Thanks for informing us that this shift has started.");

            return autoGraphResponse;
        }

        throw new AppException("This shift has not been updated to indicate it has started. Please try again.");
    }



    public AutoGraphResponse endAShift(User user, EndAShiftRequest endAShiftRequest) throws AppException {
        Shift shift = this.shiftRepository.getShiftByScheduleIdShiftIdAndJobId(
                endAShiftRequest.getCasualJobScheduleId(),
                endAShiftRequest.getShiftId(),
                endAShiftRequest.getCasualJobId());
        CasualJob casualJob = this.casualJobRepository.getById(shift.getCasualJobId());
        if(
                Arrays.asList(
                        new String[]{ShiftStatus.EMPLOYER_STARTED_SHIFT.name()}
                ).contains(shift.getShiftStatus().name()) &&
                        user.getUserRole().equals(Role.EMPLOYER) &&
                        casualJob.getSubmittedByUserId().equals(user.getUserId()))
        {
            shift.setShiftStatus(ShiftStatus.EMPLOYER_ENDED_SHIFT);
            shift.setEmployerSignedOffAt(LocalDateTime.now());
            shift.setRatingByEmployer(endAShiftRequest.getShiftRating());
            shift.setFeedbackAboutEmployee(endAShiftRequest.getFeedbackDetails());
            this.shiftRepository.save(shift);

            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
            autoGraphResponse.setStatus(0);
            autoGraphResponse.setStatusMessage("Thanks for informing us that this shift has ended.");

            return autoGraphResponse;
        }
        else if(
                Arrays.asList(
                        new String[]{ShiftStatus.EMPLOYER_STARTED_SHIFT.name(), ShiftStatus.EMPLOYER_ENDED_SHIFT.name()}
                ).contains(shift.getShiftStatus().name()) &&
                        user.getUserRole().equals(Role.EMPLOYEE) &&
                        shift.getEmployeeUserId().equals(user.getUserId()))
        {
            shift.setShiftStatus(ShiftStatus.EMPLOYEE_ENDED_SHIFT);
            shift.setEmployeeSignedInAt(LocalDateTime.now());
            shift.setEmployeeLongitudeOnShiftEnd(endAShiftRequest.getLongitude());
            shift.setEmployeeLatitudeOnShiftEnd(endAShiftRequest.getLatitude());
            this.shiftRepository.save(shift);

            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
            autoGraphResponse.setStatus(0);
            autoGraphResponse.setStatusMessage("Thanks for confirming that this shift has started.");

            return autoGraphResponse;
        }

        throw new AppException("You can not end this shift. You need to be the owner of the job or the employee assigned to the job.");
    }
}
