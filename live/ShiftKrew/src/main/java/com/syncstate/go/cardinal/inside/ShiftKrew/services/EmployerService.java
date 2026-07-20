package com.syncstate.go.cardinal.inside.ShiftKrew.services;


import com.syncstate.go.cardinal.inside.ShiftKrew.enums.*;
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
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployerService {

    @Autowired
    UserEmployerRepository userEmployerRepository;

    @Autowired
    EmployerTeamRepository employerTeamRepository;

    @Autowired
    EmployerTeamMemberRepository employerTeamMemberRepository;

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
    @Autowired
    private BidRepository bidRepository;


    @Value("${holiday.pay.percentage}")
    private double holidayPayPercent;

    @Value("${insurance.pension.percentage}")
    private double totalInsurancePensionPercent;

    @Value("${shift.krew.charge.percentage}")
    private double shiftKrewChargePercent;

    @Value("${shift.krew.vat.percentage}")
    private double shiftKrewVAT;

    @Value("${pay.to.bank.account.id}")
    private Long payToBankAccountId;

    DecimalFormat f = new DecimalFormat("##.00");
    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public AutoGraphResponse addEmployerToUserAccount(User user, AddEmployerRequest addEmployerRequest) {

        System.out.println(11);
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


    @Transactional
    public AutoGraphResponse postACasualJob(User user, PostAJobRequest postAJobRequest) throws AppException {

        //Double totalWages = 0.00;
        Double totalInsurancePension = 0.00;
        Double totalHolidayPay = 0.00;
        Double totalOtherAmount = 0.00;

        if(!user.getUserRole().equals(Role.EMPLOYER))
            throw new AppException("Only employers can create a casual job.");

        UserEmployer userEmployer = this.userEmployerRepository.getById(postAJobRequest.getUserEmployerId());

        CasualJob casualJob = new CasualJob();
        casualJob.setJobDetails(postAJobRequest.getJobDetails());
        casualJob.setJobTitle(postAJobRequest.getJobTitle());
        casualJob.setJobLineAddress(postAJobRequest.getJobLineAddress());
        casualJob.setJobAddressPostCode(postAJobRequest.getJobAddressPostCode());
        casualJob.setDressCode(postAJobRequest.getDressCode());
        casualJob.setAutoSelectFromFavorite(postAJobRequest.getAutoSelectFromFavorite());
        casualJob.setSkillId(postAJobRequest.getSkillId());
        casualJob.setCasualJobStatus(CasualJobStatus.OPEN);
        casualJob.setSubmittedByUserEmployerId(userEmployer.getUserEmployerId());
        casualJob.setSubmittedByUserId(userEmployer.getUserId());
        casualJob.setDressCode(postAJobRequest.getDressCode());
        casualJob.setContactPerson(postAJobRequest.getContactPerson());
        casualJob.setAutoSelectFromFavorite(postAJobRequest.getAutoSelectFromFavorite()==null ? false : postAJobRequest.getAutoSelectFromFavorite());
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
            invoice.setTotalWages(totalWagesPerHour);
            invoice.setTotalInsurancePension(totalInsurancePension);
            invoice.setTotalHolidayPay(totalHolidayPay);
            invoice.setTotalOtherAmount(totalOtherAmount);
            invoice.setPayToBankAccountId(payToBankAccountId);
            invoice.setIsCredit(false);
            invoice.setInvoiceStatus(InvoiceStatus.ISSUED);
            Invoice invoiceCreated = (Invoice)invoiceRepository.save(invoice);

            Double totalEmployeePay = postAJobRequest.getJobSchedule().stream().mapToDouble(js -> {
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

                return
                        js.getPayPerHour() * workPeriodInHrs * js.getEmployeesNeeded()
                        +
                        js.getPayPerHour() * workPeriodInHrs * js.getEmployeesNeeded() * holidayPayPercent / 100
                        +
                        (((js.getPayPerHour() * workPeriodInHrs) + (js.getPayPerHour() * workPeriodInHrs * js.getEmployeesNeeded() * holidayPayPercent / 100)) * totalInsurancePensionPercent / 100);
            }).sum();


            InvoiceItem invoiceItemServiceCharge = new InvoiceItem();
            invoiceItemServiceCharge.setInvoiceId(invoiceCreated.getInvoiceId());
            invoiceItemServiceCharge.setItemDescription("ShiftKrew charges");
            invoiceItemServiceCharge.setItemDescription2(shiftKrewChargePercent + "% of total cost");
            invoiceItemServiceCharge.setQty(1);
            invoiceItemServiceCharge.setRate(shiftKrewChargePercent * totalEmployeePay / 100);
            invoiceItemServiceCharge.setTotalAmount(shiftKrewChargePercent * totalEmployeePay / 100);
            this.invoiceItemRepository.save(invoiceItemServiceCharge);


            InvoiceItem invoiceItemVAT = new InvoiceItem();
            invoiceItemVAT.setInvoiceId(invoiceCreated.getInvoiceId());
            invoiceItemVAT.setItemDescription("VAT");
            invoiceItemVAT.setItemDescription2(shiftKrewVAT + "%");
            invoiceItemVAT.setQty(1);
            invoiceItemVAT.setRate((invoiceItemServiceCharge.getTotalAmount() + totalEmployeePay) * shiftKrewVAT / 100);
            invoiceItemVAT.setTotalAmount((invoiceItemServiceCharge.getTotalAmount() + totalEmployeePay) * shiftKrewVAT / 100);
            this.invoiceItemRepository.save(invoiceItemVAT);


            CasualJobDTO casualJobDTO = new CasualJobDTO();
            BeanUtils.copyProperties(casualJob, casualJobDTO);
            casualJobDTO.setEmployerJoinDate(df.format(userEmployer.getCreatedAt().toLocalDate()));
            casualJobDTO.setPostByEmployer(userEmployer.getEmployerName());
            casualJobDTO.setContactPerson(postAJobRequest.getContactPerson());
            casualJobDTO.setSkillRequired(postAJobRequest.getSkillName());


            List<CasualJobScheduleDTO> casualJobScheduleDTOList =  casualJobScheduleList.stream().map(cjs -> {
                CasualJobScheduleDTO casualJobScheduleDTO = new CasualJobScheduleDTO();
                casualJobScheduleDTO.setBonusPerHour(cjs.getBonusPerHour());
                casualJobScheduleDTO.setScheduleEndDate(cjs.getScheduleEndDate());
                casualJobScheduleDTO.setScheduleStartDate(cjs.getScheduleStartDate());
                casualJobScheduleDTO.setPayPerHour(cjs.getPayPerHour());
                casualJobScheduleDTO.setBonusPerHour(cjs.getBonusPerHour());
                casualJobScheduleDTO.setEmployeesNeeded(cjs.getEmployeesNeeded());
                return casualJobScheduleDTO;
            }).collect(Collectors.toList());

            casualJobDTO.setCasualJobSchedule(casualJobScheduleDTOList);


            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
            autoGraphResponse.setStatus(0);
            autoGraphResponse.setStatusMessage("Your job has been posted.");
            autoGraphResponse.setResponseData(casualJobDTO);

            return autoGraphResponse;
        }


        throw new AppException("We could not post your job. Please try again.");


    }


    @Transactional
    public AutoGraphResponse previewInvoice(User user, PostAJobRequest postAJobRequest) throws AppException {

        //Double totalWages = 0.00;
        Double totalInsurancePension = 0.00;
        Double totalHolidayPay = 0.00;
        Double totalOtherAmount = 0.00;

        UserEmployer userEmployer = this.userEmployerRepository.getById(postAJobRequest.getUserEmployerId());

        List<CasualJobSchedule> casualJobScheduleList = postAJobRequest.getJobSchedule().stream().map(js -> {
            CasualJobSchedule casualJobSchedule = new CasualJobSchedule();
            casualJobSchedule.setBonusPerHour(js.getBonusPerHour());
            casualJobSchedule.setPayPerHour(js.getPayPerHour());
            casualJobSchedule.setScheduleEndDate(js.getScheduleEndDate());
            casualJobSchedule.setScheduleStartDate(js.getScheduleStartDate());
            casualJobSchedule.setEmployeesNeeded(js.getEmployeesNeeded());
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
            invoice.setUserEmployerId(userEmployer.getUserEmployerId());
            invoice.setTotalWages(totalWagesPerHour);
            invoice.setTotalInsurancePension(totalInsurancePension);
            invoice.setTotalHolidayPay(totalHolidayPay);
            invoice.setTotalOtherAmount(totalOtherAmount);
            invoice.setInvoiceStatus(InvoiceStatus.DRAFT);
            invoice.setIsCredit(false);

            Double totalEmployeePay = postAJobRequest.getJobSchedule().stream().mapToDouble(js -> {
                DecimalFormat f = new DecimalFormat("##.00");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Duration d = Duration.between(js.getScheduleStartDate(), js.getScheduleEndDate());
                double workPeriodInHrs = Math.round(d.toMinutes() / 60);
                return
                        js.getPayPerHour() * workPeriodInHrs * js.getEmployeesNeeded()
                        +
                        js.getPayPerHour() * workPeriodInHrs * js.getEmployeesNeeded() * holidayPayPercent / 100
                        +
                        (((js.getPayPerHour() * workPeriodInHrs) + (js.getPayPerHour() * workPeriodInHrs * js.getEmployeesNeeded() * holidayPayPercent / 100)) * totalInsurancePensionPercent / 100);
            }).sum();



            List<List<InvoiceItem>> invoiceItemList = postAJobRequest.getJobSchedule().stream().map(js -> {
                Duration d = Duration.between(js.getScheduleStartDate(), js.getScheduleEndDate());
                double workPeriodInHrs = Math.round(d.toMinutes() / 60);

                InvoiceItem invoiceItem = new InvoiceItem();
                invoiceItem.setItemDescription("Wages - " + f.format(workPeriodInHrs) + " hours");
                invoiceItem.setItemDescription2(df.format(js.getScheduleStartDate())  +  ": " + postAJobRequest.getSkillName());
                invoiceItem.setQty(js.getEmployeesNeeded());
                invoiceItem.setRate(js.getPayPerHour());
                invoiceItem.setTotalAmount(js.getPayPerHour() * workPeriodInHrs * js.getEmployeesNeeded());

                InvoiceItem invoiceItem1 = new InvoiceItem();
                invoiceItem1.setItemDescription("Holiday Pay");
                invoiceItem1.setItemDescription2(df.format(js.getScheduleStartDate())  +  ": " + f.format(holidayPayPercent) + "% of wages");
                invoiceItem1.setQty(js.getEmployeesNeeded());
                invoiceItem1.setRate(js.getPayPerHour());
                invoiceItem1.setTotalAmount(js.getPayPerHour() * workPeriodInHrs * js.getEmployeesNeeded() * holidayPayPercent / 100);

                InvoiceItem invoiceItem2 = new InvoiceItem();
                invoiceItem2.setItemDescription("NI and Pension");
                invoiceItem2.setItemDescription2(df.format(js.getScheduleStartDate())  +  ": " + f.format(totalInsurancePensionPercent) + "% of total earnings");
                invoiceItem2.setQty(js.getEmployeesNeeded());
                invoiceItem2.setRate(js.getPayPerHour());
                invoiceItem2.setTotalAmount(((js.getPayPerHour() * workPeriodInHrs) + (js.getPayPerHour() * workPeriodInHrs * js.getEmployeesNeeded() * holidayPayPercent / 100)) * totalInsurancePensionPercent / 100);

                List<InvoiceItem> li = new ArrayList<>();
                li.add(invoiceItem);
                li.add(invoiceItem1);
                li.add(invoiceItem2);

                return li;
            }).collect(Collectors.toList());


            List<InvoiceItem> li = new ArrayList<>();
            InvoiceItem invoiceItemServiceCharge = new InvoiceItem();
            invoiceItemServiceCharge.setItemDescription("ShiftKrew charges");
            invoiceItemServiceCharge.setItemDescription2(shiftKrewChargePercent + "% of total cost");
            invoiceItemServiceCharge.setQty(1);
            invoiceItemServiceCharge.setRate(shiftKrewChargePercent * totalEmployeePay / 100);
            invoiceItemServiceCharge.setTotalAmount(shiftKrewChargePercent * totalEmployeePay / 100);
            li.add(invoiceItemServiceCharge);


            InvoiceItem invoiceItemVAT = new InvoiceItem();
            invoiceItemVAT.setItemDescription("VAT");
            invoiceItemVAT.setItemDescription2(shiftKrewVAT + "%");
            invoiceItemVAT.setQty(1);
            invoiceItemVAT.setRate((invoiceItemServiceCharge.getTotalAmount() + totalEmployeePay) * shiftKrewVAT / 100);
            invoiceItemVAT.setTotalAmount((invoiceItemServiceCharge.getTotalAmount() + totalEmployeePay) * shiftKrewVAT / 100);
            li.add(invoiceItemVAT);

            invoiceItemList.add(li);




            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
            autoGraphResponse.setStatus(0);
            autoGraphResponse.setStatusMessage("Invoice for service");
            autoGraphResponse.setResponseData(invoiceItemList);

            return autoGraphResponse;
        }


        throw new AppException("We could not provide an invoice at the moment.");


    }

    @Transactional
    public AutoGraphResponse cancelACasualJob(User user, CancelAJobRequest cancelAJobRequest) throws AppException {
        UserEmployer userEmployer = this.userEmployerRepository.getById(cancelAJobRequest.getUserEmployerId());
        if(userEmployer==null)
            throw new AppException("We could not detect your identity. Please log out and login again before repeating this action");

        CasualJob casualJob = this.casualJobRepository.getById(cancelAJobRequest.getCasualJobId());
        casualJob.setCasualJobStatus(CasualJobStatus.CANCELLED);
        casualJob.setReasonForCancellation(cancelAJobRequest.getReasonForCancellation());
        casualJob = (CasualJob) casualJobRepository.save(casualJob);

        Collection<Invoice> invoiceList = this.invoiceRepository.getInvoicesByCreditFlag(true);
        int invoiceNumber = invoiceList.size() + 1;
        String invoiceNumberPadded = StringUtils.left(Integer.toString(invoiceNumber), 4);
        Collection<CasualJobSchedule> casualJobScheduleCollection =
                this.casualJobScheduleRepository.getCasualJobScheduleByCasualJobId(casualJob.getCasualJobId());

        Optional<CasualJobSchedule> casualJobScheduleOpt = casualJobScheduleCollection.stream().filter(t -> t.getScheduleStartDate().getDayOfYear() ==
                LocalDate.now().getDayOfYear()
        ).findFirst();
        if(casualJobScheduleOpt.isEmpty())
        {
            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
            autoGraphResponse.setStatus(0);
            autoGraphResponse.setStatusMessage("Your casual job has been cancelled without any charge to your organization.");
            return autoGraphResponse;
        }
        CasualJobSchedule casualJobSchedule = casualJobScheduleOpt.get();



        Invoice oldInvoice = (Invoice) this.invoiceRepository.
                getInvoiceByCasualJobIdAndStatus(casualJob.getCasualJobId(),
                        InvoiceStatus.ISSUED
        );


        double totalWagesPerHour = 1 * 60 * casualJobSchedule.getEmployeesNeeded() * casualJobSchedule.getPayPerHour()/60;
        long totalMinutes = 1*60;
        Double totalHolidayPay = holidayPayPercent * totalWagesPerHour/100;
        Double totalInsurancePension = (totalHolidayPay + totalWagesPerHour) * totalInsurancePensionPercent;
        double totalOtherAmount = 0.00;

        Invoice invoice = new Invoice();
        invoice.setCasualJobDate(LocalDate.now());
        invoice.setInvoiceDescription("Employer Refund Due To Cancellation - "+ casualJob.getJobTitle());
        invoice.setInvoiceNumber("INV-" +
                Integer.toString(new Date().getYear())
                + "-" + invoiceNumberPadded
        );
        invoice.setBillToAddressCity(userEmployer.getEmployerContactAddressCity());
        invoice.setBillToAddressState(userEmployer.getEmployerContactAddressState());
        invoice.setBillToAddressCountry(userEmployer.getEmployerContactAddressCountry());
        invoice.setBillToPostCodeAddress(userEmployer.getEmployerContactAddressPostCode());
        invoice.setBillToLineAddress(userEmployer.getEmployerContactAddress());
        invoice.setBillToFullName(user.getFirstName() + " " + user.getLastName());
        invoice.setCasualJobId(casualJob.getCasualJobId());
        invoice.setUserEmployerId(userEmployer.getUserEmployerId());
        invoice.setTotalWages(totalWagesPerHour);
        invoice.setTotalInsurancePension(totalInsurancePension);
        invoice.setTotalHolidayPay(totalHolidayPay);
        invoice.setTotalOtherAmount(totalOtherAmount);
        invoice.setPayToBankAccountId(payToBankAccountId);
        invoice.setIsCredit(true);
        invoice.setInvoiceStatus(InvoiceStatus.ISSUED);
        Invoice invoiceCreated = (Invoice)invoiceRepository.save(invoice);

        oldInvoice.setInvoiceStatus(InvoiceStatus.CANCELLED);
        invoiceRepository.save(oldInvoice);


        AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
        autoGraphResponse.setStatus(0);
        autoGraphResponse.setStatusMessage("Your casual job has been cancelled.");
        return autoGraphResponse;
    }

    public AutoGraphResponse addBusinessToUser(User user, AddBusinessToUserRequest addBusinessToUserRequest) throws AppException {
        if(user.getUserRole()!= Role.EMPLOYER)
        {
            throw new AppException("You are currently logged in as an Employee. Please log in as an Employer.");
        }

        UserEmployer userEmployer = new UserEmployer();
        BeanUtils.copyProperties(addBusinessToUserRequest, userEmployer);
        userEmployer.setUserId(user.getUserId());

        this.userEmployerRepository.save(userEmployer);


        AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
        autoGraphResponse.setStatus(0);
        autoGraphResponse.setStatusMessage("Your business has been mapped to your ShiftKrew account successfully.");
        return autoGraphResponse;
    }

    public AutoGraphResponse updateACasualJob(User user, UpdateAJobRequest updateAJobRequest) throws AppException {

        //Double totalWages = 0.00;
        Double totalInsurancePension = 0.00;
        Double totalHolidayPay = 0.00;
        Double totalOtherAmount = 0.00;

        if(!user.getUserRole().equals(Role.EMPLOYER))
            throw new AppException("Only employers can create a casual job.");

        CasualJob casualJob = this.casualJobRepository.getById(updateAJobRequest.getCasualJobId());

        UserEmployer userEmployer = this.userEmployerRepository.getById(casualJob.getSubmittedByUserEmployerId());
        if(!(userEmployer!=null && userEmployer.getUserId().equals(user.getUserId())))
            throw new AppException("Only the employer who posted this casual job can update this job.");


        TaskSchedule taskSchedule_ = this.taskScheduleRepository.getTaskScheduleByTemplateCasualJobId(casualJob.getCasualJobId());
        if(taskSchedule_!=null)
        {
            this.taskScheduleRepository.delete(taskSchedule_);
        }

        Collection<CasualJobSchedule> casualJobScheduleListExist = this.casualJobScheduleRepository.getCasualJobScheduleByCasualJobId(casualJob.getCasualJobId());
        if(casualJobScheduleListExist!=null)
        {
            casualJobScheduleListExist.stream().map(cjs -> {
                this.casualJobScheduleRepository.delete(cjs);
                return cjs;
            }).collect(Collectors.toList());
        }

        Collection<InvoiceItem> invoiceItemList = this.invoiceItemRepository.getByCasualJobId(casualJob.getCasualJobId());
        invoiceItemList.stream().map(iil -> {
            this.invoiceItemRepository.delete(iil);
            return iil;
        }).collect(Collectors.toList());

        Collection<Invoice> invoiceList = this.invoiceRepository.getInvoiceByCasualJobId(casualJob.getCasualJobId());
        invoiceList.stream().map(il -> {
            this.invoiceRepository.delete(il);
            return il;
        }).collect(Collectors.toList());


        casualJob.setJobDetails(updateAJobRequest.getJobDetails());
        casualJob.setJobTitle(updateAJobRequest.getJobTitle());
        casualJob.setJobLineAddress(updateAJobRequest.getJobLineAddress());
        casualJob.setJobAddressPostCode(updateAJobRequest.getJobAddressPostCode());
        casualJob.setDressCode(updateAJobRequest.getDressCode());
        casualJob.setAutoSelectFromFavorite(updateAJobRequest.getAutoSelectFromFavorite());
        casualJob.setSkillId(updateAJobRequest.getSkillId());
        casualJob.setCasualJobStatus(CasualJobStatus.OPEN);
        casualJob.setSubmittedByUserEmployerId(userEmployer.getUserEmployerId());
        casualJob.setSubmittedByUserId(userEmployer.getUserId());
        casualJob.setDressCode(updateAJobRequest.getDressCode());
        casualJob.setContactPerson(updateAJobRequest.getContactPerson());
        casualJob.setAutoSelectFromFavorite(updateAJobRequest.getAutoSelectFromFavorite()==null ? false : updateAJobRequest.getAutoSelectFromFavorite());
        CasualJob casualJobCreated = (CasualJob) casualJobRepository.save(casualJob);

        if(updateAJobRequest.getAutoPostThisForTheNextNthWeeks()!=null)
        {
            TaskSchedule taskSchedule = new TaskSchedule();
            taskSchedule.setTaskScheduleIntervalType(TaskScheduleIntervalType.WEEK);
            taskSchedule.setIntervalPeriod(1);
            taskSchedule.setTaskScheduleType(TaskScheduleType.POST_CASUAL_JOB);
            taskSchedule.setIsActive(true);
            taskSchedule.setTemplateCasualJobId(casualJobCreated.getCasualJobId());
            taskSchedule.setPeriodTaskRuns(updateAJobRequest.getAutoPostThisForTheNextNthWeeks());
            taskSchedule = (TaskSchedule) taskScheduleRepository.save(taskSchedule);
        }

        List<CasualJobSchedule> casualJobScheduleList = updateAJobRequest.getJobSchedule().stream().map(js -> {
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

        double totalWagesPerHour = updateAJobRequest.getJobSchedule().stream().mapToDouble(js -> {
            Duration d = Duration.between(js.getScheduleStartDate(), js.getScheduleEndDate());
            long minutes = d.toMinutes();
            return minutes * js.getEmployeesNeeded() * js.getPayPerHour()/60;
        }).sum();
        long totalMinutes = updateAJobRequest.getJobSchedule().stream().mapToLong(js -> {
            Duration d = Duration.between(js.getScheduleStartDate(), js.getScheduleEndDate());
            long minutes = d.toMinutes();
            return minutes;
        }).sum();
        totalHolidayPay = holidayPayPercent * totalWagesPerHour/100;
        totalInsurancePension = (totalHolidayPay + totalWagesPerHour) * totalInsurancePensionPercent;

        if(casualJobScheduleList!=null && casualJobScheduleList.size()>0)
        {

            List<Invoice> employerInvoices = this.invoiceRepository.getInvoicesByEmployerId(updateAJobRequest.getUserEmployerId());
            int invoiceNumber = employerInvoices.size() + 1;
            String invoiceNumberPadded = StringUtils.left(Integer.toString(invoiceNumber), 4);

            Invoice invoice = new Invoice();
            invoice.setCasualJobDate(casualJobScheduleList.get(0).getScheduleStartDate().toLocalDate());
            invoice.setInvoiceDescription("Employee recruitment - "+ updateAJobRequest.getSkillName());
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
            invoice.setTotalWages(totalWagesPerHour);
            invoice.setTotalInsurancePension(totalInsurancePension);
            invoice.setTotalHolidayPay(totalHolidayPay);
            invoice.setTotalOtherAmount(totalOtherAmount);
            invoice.setPayToBankAccountId(payToBankAccountId);
            invoice.setIsCredit(false);
            invoice.setInvoiceStatus(InvoiceStatus.ISSUED);
            Invoice invoiceCreated = (Invoice)invoiceRepository.save(invoice);

            Double totalEmployeePay = updateAJobRequest.getJobSchedule().stream().mapToDouble(js -> {
                Duration d = Duration.between(js.getScheduleStartDate(), js.getScheduleEndDate());
                double workPeriodInHrs = Math.round(d.toMinutes() / 60);

                InvoiceItem invoiceItem = new InvoiceItem();
                invoiceItem.setInvoiceId(invoiceCreated.getInvoiceId());
                invoiceItem.setItemDescription("Wages - " + f.format(workPeriodInHrs) + " hours");
                invoiceItem.setItemDescription2(df.format(js.getScheduleStartDate())  +  ": " + updateAJobRequest.getSkillName());
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

                return
                        js.getPayPerHour() * workPeriodInHrs * js.getEmployeesNeeded()
                                +
                                js.getPayPerHour() * workPeriodInHrs * js.getEmployeesNeeded() * holidayPayPercent / 100
                                +
                                (((js.getPayPerHour() * workPeriodInHrs) + (js.getPayPerHour() * workPeriodInHrs * js.getEmployeesNeeded() * holidayPayPercent / 100)) * totalInsurancePensionPercent / 100);
            }).sum();


            InvoiceItem invoiceItemServiceCharge = new InvoiceItem();
            invoiceItemServiceCharge.setInvoiceId(invoiceCreated.getInvoiceId());
            invoiceItemServiceCharge.setItemDescription("ShiftKrew charges");
            invoiceItemServiceCharge.setItemDescription2(shiftKrewChargePercent + "% of total cost");
            invoiceItemServiceCharge.setQty(1);
            invoiceItemServiceCharge.setRate(shiftKrewChargePercent * totalEmployeePay / 100);
            invoiceItemServiceCharge.setTotalAmount(shiftKrewChargePercent * totalEmployeePay / 100);
            this.invoiceItemRepository.save(invoiceItemServiceCharge);


            InvoiceItem invoiceItemVAT = new InvoiceItem();
            invoiceItemVAT.setInvoiceId(invoiceCreated.getInvoiceId());
            invoiceItemVAT.setItemDescription("VAT");
            invoiceItemVAT.setItemDescription2(shiftKrewVAT + "%");
            invoiceItemVAT.setQty(1);
            invoiceItemVAT.setRate((invoiceItemServiceCharge.getTotalAmount() + totalEmployeePay) * shiftKrewVAT / 100);
            invoiceItemVAT.setTotalAmount((invoiceItemServiceCharge.getTotalAmount() + totalEmployeePay) * shiftKrewVAT / 100);
            this.invoiceItemRepository.save(invoiceItemVAT);


            CasualJobDTO casualJobDTO = new CasualJobDTO();
            BeanUtils.copyProperties(casualJob, casualJobDTO);
            casualJobDTO.setEmployerJoinDate(df.format(userEmployer.getCreatedAt().toLocalDate()));
            casualJobDTO.setPostByEmployer(userEmployer.getEmployerName());
            casualJobDTO.setContactPerson(updateAJobRequest.getContactPerson());
            casualJobDTO.setSkillRequired(updateAJobRequest.getSkillName());


            List<CasualJobScheduleDTO> casualJobScheduleDTOList =  casualJobScheduleList.stream().map(cjs -> {
                CasualJobScheduleDTO casualJobScheduleDTO = new CasualJobScheduleDTO();
                casualJobScheduleDTO.setBonusPerHour(cjs.getBonusPerHour());
                casualJobScheduleDTO.setScheduleEndDate(cjs.getScheduleEndDate());
                casualJobScheduleDTO.setScheduleStartDate(cjs.getScheduleStartDate());
                casualJobScheduleDTO.setPayPerHour(cjs.getPayPerHour());
                casualJobScheduleDTO.setBonusPerHour(cjs.getBonusPerHour());
                casualJobScheduleDTO.setEmployeesNeeded(cjs.getEmployeesNeeded());
                return casualJobScheduleDTO;
            }).collect(Collectors.toList());

            casualJobDTO.setCasualJobSchedule(casualJobScheduleDTOList);


            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
            autoGraphResponse.setStatus(0);
            autoGraphResponse.setStatusMessage("Your job has been posted.");
            autoGraphResponse.setResponseData(casualJobDTO);

            return autoGraphResponse;
        }


        throw new AppException("We could not post your job. Please try again.");


    }

    public AutoGraphResponse getACasualJob(User user, Long casualJobId) {
        CasualJob casualJob = this.casualJobRepository.getById(casualJobId);
        Collection<Bid> bids = this.bidRepository.getBidByCasualJobId(casualJobId);
        Collection<CasualJobSchedule> casualJobScheduleCollection = this.casualJobScheduleRepository.
                getCasualJobScheduleByCasualJobId(casualJob.getCasualJobId());

        HashMap<String, Object> hMap = new HashMap<>();
        hMap.put("casualJob", casualJob);
        hMap.put("bids", bids);
        hMap.put("casualJobSchedule", casualJobScheduleCollection);

        AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
        autoGraphResponse.setStatus(0);
        autoGraphResponse.setStatusMessage("Casual Job details.");
        autoGraphResponse.setResponseData(hMap);

        return autoGraphResponse;
    }

    public AutoGraphResponse createNewTeam(User user, CreateNewTeamRequest createNewTeamRequest) throws AppException {
        EmployerTeam employerTeam = this.employerTeamRepository.getEmployerTeamByTeamName(createNewTeamRequest.getTeamName(), user.getUserId());
        if(employerTeam!=null)
        {
            throw new AppException("You already have a team with a similar team name");
        }

        employerTeam = new EmployerTeam();
        employerTeam.setTeamName(createNewTeamRequest.getTeamName());
        employerTeam.setCreatedByUserId(user.getUserId());
        EmployerTeam employerTeamCreated = (EmployerTeam) this.employerTeamRepository.save(employerTeam);
        createNewTeamRequest.getUserIdList().stream().map(u -> {
            EmployerTeamMember employerTeamMember = new EmployerTeamMember();
            employerTeamMember.setEmployerTeamId(employerTeamCreated.getEmployerTeamId());
            employerTeamMember.setEmployeeUserId(u);
            employerTeamMember = (EmployerTeamMember)this.employerTeamMemberRepository.save(employerTeamMember);
            return employerTeamMember;
        }).collect(Collectors.toList());

        AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
        autoGraphResponse.setStatus(0);
        autoGraphResponse.setStatusMessage("A new team has been created");
        autoGraphResponse.setResponseData(employerTeamCreated);

        return autoGraphResponse;
    }

    public AutoGraphResponse addMemberToTeam(User user, AddNewTeamMemberRequest addNewTeamMemberRequest) {
        List<EmployerTeamMember> teamMembersAdded = addNewTeamMemberRequest.getUserIdList().stream().map(u -> {
            EmployerTeamMember employerTeamMember = new EmployerTeamMember();
            employerTeamMember.setEmployerTeamId(addNewTeamMemberRequest.getTeamId());
            employerTeamMember.setEmployeeUserId(u);
            employerTeamMember = (EmployerTeamMember)this.employerTeamMemberRepository.save(employerTeamMember);
            return employerTeamMember;
        }).collect(Collectors.toList());

        AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
        autoGraphResponse.setStatus(0);
        autoGraphResponse.setStatusMessage("A new team member has been added.");
        autoGraphResponse.setResponseData(teamMembersAdded);

        return autoGraphResponse;
    }

    public AutoGraphResponse removeTeamMember(User user, RemoveTeamMemberRequest removeTeamMemberRequest) throws AppException {
        EmployerTeam employerTeam = this.employerTeamRepository.getById(removeTeamMemberRequest.getTeamId());
        EmployerTeamMember employerTeamMember = this.employerTeamMemberRepository.getById(removeTeamMemberRequest.getUserId());

        if(employerTeam!=null && employerTeamMember!=null &&
                employerTeamMember.getEmployerTeamId().equals(employerTeam.getEmployerTeamId()))
        {
            this.employerTeamMemberRepository.delete(employerTeamMember);
        }

        throw new AppException("Employer team member mismatch. We can not handle this request at the moment.");
    }

    public AutoGraphResponse removeTeam(User user, Long teamId) throws AppException {
        EmployerTeam employerTeam = this.employerTeamRepository.getById(teamId);
        if(employerTeam!=null && employerTeam.getCreatedByUserId().equals(user.getUserId()))
        {
            this.employerTeamRepository.delete(employerTeam);
        }
        throw new AppException("Employer team mismatch. We can not handle this request at the moment.");
    }
}
