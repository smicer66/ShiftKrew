package com.syncstate.go.cardinal.inside.ShiftKrew.services;


import com.syncstate.go.cardinal.inside.ShiftKrew.enums.TaskScheduleIntervalType;
import com.syncstate.go.cardinal.inside.ShiftKrew.enums.TaskScheduleType;
import com.syncstate.go.cardinal.inside.ShiftKrew.enums.UserStatus;
import com.syncstate.go.cardinal.inside.ShiftKrew.exceptions.AppException;
import com.syncstate.go.cardinal.inside.ShiftKrew.exceptions.InstanceExistsException;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.*;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.dto.CasualJobDTO;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.dto.CasualJobScheduleDTO;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.dto.UserDTO;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.dto.UserEmployerDTO;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.*;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.responses.AutoGraphResponse;
import com.syncstate.go.cardinal.inside.ShiftKrew.repositories.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployerService {

    @Autowired
    UserEmployerRepository userEmployerRepository;

    @Autowired
    CasualJobRepository casualJobRepository;

    @Autowired
    CasualJobScheduleRepository casualJobScheduleRepository;

    @Autowired
    TaskScheduleRepository taskScheduleRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Value("${holiday.pay.percentage}")
    private double holidayPayPercent;

    @Value("${insurance.pension.percentage}")
    private double totalInsurancePensionPercent;


    public AutoGraphResponse addEmployerToUserAccount(User user, AddEmployerRequest addEmployerRequest) {


        UserEmployer userEmployer = this.userEmployerRepository.getUserEmployerByUserIdAndEmployerName(user.getUserId(),
                addEmployerRequest.getEmployerName());

        if(userEmployer==null)
        {
            userEmployer = new UserEmployer();
            userEmployer.setEmployerName(addEmployerRequest.getEmployerName());
            userEmployer.setEmployerContactEmail(addEmployerRequest.getEmployerEmailAddress());
            userEmployer.setEmployerContactMobile(addEmployerRequest.getEmployerContactMobile());
            userEmployer.setEmployerContactAddressPostCode(addEmployerRequest.getEmployerContactAddressPostCode());
            userEmployer.setEmployerContactAddress(addEmployerRequest.getEmployerContactAddress());
            userEmployer.setEmployerContactAddressCity(addEmployerRequest.getEmployerContactAddressCity());
            userEmployer.setEmployerContactAddressState(addEmployerRequest.getEmployerContactAddressState());
            userEmployer.setEmployerContactAddressCountry(addEmployerRequest.getEmployerContactAddressCountry());
            userEmployer.setIsActive(true);
            userEmployer.setUserId(user.getUserId());

            userEmployer = (UserEmployer) userEmployerRepository.save(userEmployer);
        }

        UserEmployerDTO userEmployerDTO = new UserEmployerDTO();
        BeanUtils.copyProperties(userEmployer, userEmployerDTO);


        AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
        autoGraphResponse.setStatus(0);
        autoGraphResponse.setStatusMessage("Your employer has been added to your ShiftKrew account.");
        autoGraphResponse.setResponseData(userEmployerDTO);

        return autoGraphResponse;
    }

    public AutoGraphResponse postACasualJob(User user, PostAJobRequest postAJobRequest) throws AppException {

        Double totalWages = 0.00;
        Double totalInsurancePension = 0.00;
        Double totalHolidayPay = 0.00;
        Double totalOtherAmount = 0.00;

        UserEmployer userEmployer = this.userEmployerRepository.getById(postAJobRequest.getUserEmployerId());

        CasualJob casualJob = new CasualJob();
        casualJob.setJobDetails(postAJobRequest.getJobDetails());
        casualJob.setJobTitle(postAJobRequest.getJobTitle());
        casualJob.setJobLineAddress(postAJobRequest.getJobLineAddress());
        casualJob.setJobAddressPostCode(postAJobRequest.getJobAddressPostCode());
        casualJob.setDressCode(postAJobRequest.getDressCode());
        casualJob.setAutoSelectFromFavorite(postAJobRequest.getAutoSelectFromFavorite());
        casualJob.setSkillId(postAJobRequest.getSkillId());
        CasualJob casualJobCreated = (CasualJob) casualJobRepository.save(casualJob);

        if(postAJobRequest.getAutoSelectFromFavorite().equals(Boolean.TRUE))
        {
            TaskSchedule taskSchedule = new TaskSchedule();
            taskSchedule.setTaskScheduleIntervalType(TaskScheduleIntervalType.WEEK);
            taskSchedule.setIntervalPeriod(1);
            taskSchedule.setTaskScheduleType(TaskScheduleType.POST_CASUAL_JOB);
            taskSchedule.setIsActive(true);
            taskSchedule.setPeriodTaskRuns(postAJobRequest.getAutoPostThisForTheNextNthWeeks());
            taskSchedule = (TaskSchedule) taskScheduleRepository.save(taskSchedule);
        }

        List<CasualJobSchedule> casualJobScheduleList = postAJobRequest.getJobSchedule().stream().map(js -> {
           CasualJobSchedule casualJobSchedule = new CasualJobSchedule();
           casualJobSchedule.setJobId(casualJobCreated.getCasualJobId());
           casualJobSchedule.setBonusPerHour(js.getBonusPerHour());
           casualJobSchedule.setPayPerHour(js.getPayPerHour());
           casualJobSchedule.setScheduleEndDate(js.getScheduleEndDate());
           casualJobSchedule.setScheduleStartDate(js.getScheduleStartDate());
           casualJobSchedule.setEmployeesNeeded(js.getEmployeesNeeded());
           casualJobScheduleRepository.save(casualJobSchedule);
           return casualJobSchedule;
        }).collect(Collectors.toList());

        double totalWagesPerHour = postAJobRequest.getJobSchedule().stream().mapToDouble(js -> {
            Duration d = Duration.between(js.getScheduleStartDate(), js.getScheduleEndDate());
            long minutes = d.toMinutes();
            return minutes * js.getEmployeesNeeded() * js.getPayPerHour()/60;
        }).sum();
        long totalMinutes = postAJobRequest.getJobSchedule().stream().mapToLong(js -> {
            Duration d = Duration.between(js.getScheduleStartDate(), js.getScheduleEndDate());
            long minutes = d.toMinutes();
            return minutes;
        }).sum();
        totalHolidayPay = holidayPayPercent * totalWagesPerHour/100;
        totalInsurancePension = (totalHolidayPay + totalWagesPerHour) * totalInsurancePensionPercent;

        if(casualJobScheduleList!=null && casualJobScheduleList.size()>0)
        {

            List<Invoice> employerInvoices = this.invoiceRepository.getInvoicesByEmployerId(postAJobRequest.getUserEmployerId());
            int invoiceNumber = employerInvoices.size() + 1;
            String invoiceNumberPadded = StringUtils.left(Integer.toString(invoiceNumber), 4);

            Invoice invoice = new Invoice();
            invoice.setCasualJobDate(casualJobScheduleList.get(0).getScheduleStartDate().toLocalDate());
            invoice.setInvoiceDescription("Employee recruitment - "+ postAJobRequest.getSkillName());
            invoice.setInvoiceNumber("INV-" +
                    Integer.toString(casualJobScheduleList.get(0).getScheduleStartDate().toLocalDate().getYear())
                    + "-" + invoiceNumberPadded
            );
            invoice.setBillToAddressCity(userEmployer.getEmployerContactAddressCity());
            invoice.setBillToAddressState(userEmployer.getEmployerContactAddressState());
            invoice.setBillToAddressCountry(userEmployer.getEmployerContactAddressCountry());
            invoice.setBillToPostCodeAddress(userEmployer.getEmployerContactAddressPostCode());
            invoice.setBillToLineAddress(userEmployer.getEmployerContactAddress());
            invoice.setBillToFullName(user.getFirstName() + " " + user.getLastName());
            invoice.setCasualJobId(casualJobCreated.getCasualJobId());
            invoice.setUserEmployerId(userEmployer.getUserEmployerId());
            invoice.setTotalWages(totalWages);
            invoice.setTotalInsurancePension(totalInsurancePension);
            invoice.setTotalHolidayPay(totalHolidayPay);
            invoice.setTotalOtherAmount(totalOtherAmount);
            Invoice invoiceCreated = (Invoice)invoiceRepository.save(invoice);

            postAJobRequest.getJobSchedule().stream().map(js -> {
                DecimalFormat f = new DecimalFormat("##.00");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Duration d = Duration.between(js.getScheduleStartDate(), js.getScheduleEndDate());
                double workPeriodInHrs = Math.round(d.toMinutes() / 60);

                InvoiceItem invoiceItem = new InvoiceItem();
                invoiceItem.setInvoiceId(invoiceCreated.getInvoiceId());
                invoiceItem.setItemDescription("Wages - " + f.format(workPeriodInHrs) + " hours");
                invoiceItem.setItemDescription2(df.format(js.getScheduleStartDate())  +  ": " + postAJobRequest.getSkillName());
                invoiceItem.setQty(js.getEmployeesNeeded());
                invoiceItem.setRate(js.getPayPerHour());
                invoiceItem.setTotalAmount(js.getPayPerHour() * workPeriodInHrs * js.getEmployeesNeeded());
                this.invoiceItemRepository.save(invoiceItem);

                InvoiceItem invoiceItem1 = new InvoiceItem();
                invoiceItem1.setInvoiceId(invoiceCreated.getInvoiceId());
                invoiceItem1.setItemDescription("Holiday Pay");
                invoiceItem1.setItemDescription2(df.format(js.getScheduleStartDate())  +  ": " + f.format(holidayPayPercent) + "% of wages");
                invoiceItem1.setQty(js.getEmployeesNeeded());
                invoiceItem1.setRate(js.getPayPerHour());
                invoiceItem1.setTotalAmount(js.getPayPerHour() * workPeriodInHrs * js.getEmployeesNeeded() * holidayPayPercent / 100);
                this.invoiceItemRepository.save(invoiceItem1);


                InvoiceItem invoiceItem2 = new InvoiceItem();
                invoiceItem2.setInvoiceId(invoiceCreated.getInvoiceId());
                invoiceItem2.setItemDescription("NI and Pension");
                invoiceItem2.setItemDescription2(df.format(js.getScheduleStartDate())  +  ": " + f.format(totalInsurancePensionPercent) + "% of total earnings");
                invoiceItem2.setQty(js.getEmployeesNeeded());
                invoiceItem2.setRate(js.getPayPerHour());
                invoiceItem2.setTotalAmount(((js.getPayPerHour() * workPeriodInHrs) + (js.getPayPerHour() * workPeriodInHrs * js.getEmployeesNeeded() * holidayPayPercent / 100)) * totalInsurancePensionPercent / 100);
                this.invoiceItemRepository.save(invoiceItem2);

                return null;
            });


            CasualJobDTO casualJobDTO = new CasualJobDTO();
            BeanUtils.copyProperties(casualJob, casualJobDTO);

            List<CasualJobScheduleDTO> casualJobScheduleDTOList = new ArrayList<>();
            BeanUtils.copyProperties(casualJobScheduleList, casualJobScheduleDTOList);

            casualJobDTO.setCasualJobSchedule(casualJobScheduleDTOList);


            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
            autoGraphResponse.setStatus(0);
            autoGraphResponse.setStatusMessage("Your job has been posted.");
            autoGraphResponse.setResponseData(casualJobDTO);

            return autoGraphResponse;
        }


        throw new AppException("We could not post your job. Please try again.");


    }
}
