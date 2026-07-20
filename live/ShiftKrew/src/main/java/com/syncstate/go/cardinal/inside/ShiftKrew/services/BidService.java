package com.syncstate.go.cardinal.inside.ShiftKrew.services;


import com.syncstate.go.cardinal.inside.ShiftKrew.enums.BidStatus;
import com.syncstate.go.cardinal.inside.ShiftKrew.enums.CasualJobStatus;
import com.syncstate.go.cardinal.inside.ShiftKrew.enums.ShiftStatus;
import com.syncstate.go.cardinal.inside.ShiftKrew.exceptions.AppException;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.*;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.dto.BidDTO;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.CancelAShiftRequest;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.PostABidRequest;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.SelectWinningBidRequest;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.UpdateABidRequest;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.responses.AutoGraphResponse;
import com.syncstate.go.cardinal.inside.ShiftKrew.repositories.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BidService {

    @Autowired
    UserEmployerRepository userEmployerRepository;

    @Autowired
    BidRepository bidRepository;

    @Autowired
    CasualJobRepository casualJobRepository;

    @Autowired
    CasualJobScheduleRepository casualJobScheduleRepository;

    @Autowired
    CasualJobScheduleBidRepository casualJobScheduleBidRepository;

    @Autowired
    ShiftRepository shiftRepository;



    DecimalFormat f = new DecimalFormat("##.00");
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy MM dd");

    public AutoGraphResponse postABid(User user, PostABidRequest postABidRequest) throws AppException {

        Bid bid = this.bidRepository.getBidByCasualJobIdAndBidderId(user.getUserId(), postABidRequest.getCasualJobId());
        CasualJob casualJob = this.casualJobRepository.getById(postABidRequest.getCasualJobId());
        UserEmployer userEmployer = this.userEmployerRepository
                .getUserEmployerByUserEmployerId(casualJob.getSubmittedByUserEmployerId());

        if(casualJob!=null && casualJob.getCasualJobStatus().equals(CasualJobStatus.OPEN))
        {

            Collection<CasualJobSchedule> casualJobSchedules =
                this.casualJobScheduleRepository.getCasualJobScheduleByCasualJobScheduleId(
                        postABidRequest.getBidScheduleIdList()
                );
            List<Long> casualJobScheduleIds = casualJobSchedules.stream().
                    map(cjs -> cjs.getCasualJobScheduleId()).
                    collect(Collectors.toList());

            if(bid==null)
            {
                bid = new Bid();
            }
            else
            {
                Collection<CasualJobScheduleBid> casualJobScheduleBids = this.casualJobScheduleBidRepository
                        .getCasualJobScheduleBidByScheduleIdAndBidId(
                                bid.getBidId(),
                                casualJobScheduleIds

                );
                casualJobScheduleBids.stream().map(cjs -> {
                    this.casualJobScheduleBidRepository.delete(cjs);
                    return null;
                }).collect(Collectors.toList());
            }
            bid.setBidAmountPerHour(postABidRequest.getBidAmountPerHour());
            bid.setBidDetails(postABidRequest.getBidDetails());
            bid.setBidStatus(BidStatus.ACTIVE);
            bid.setCasualJobId(casualJob.getCasualJobId());
            bid.setBidSubmittedByUserId(user.getUserId());
            Bid bidCreated = (Bid) this.bidRepository.save(bid);






            System.out.println(casualJobSchedules.size());
            casualJobSchedules.stream().map(bs -> {
                CasualJobScheduleBid casualJobScheduleBid = new CasualJobScheduleBid();
                casualJobScheduleBid.setBidOwnerId(bidCreated.getBidSubmittedByUserId());
                casualJobScheduleBid.setJobId(bidCreated.getCasualJobId());
                casualJobScheduleBid.setJobScheduleId(bs.getCasualJobScheduleId());
                casualJobScheduleBid.setBidId(bidCreated.getBidId());
                casualJobScheduleBid = (CasualJobScheduleBid)this.casualJobScheduleBidRepository.save(casualJobScheduleBid);

                return casualJobScheduleBid;
            }).collect(Collectors.toList());


            BidDTO bidDTO = new BidDTO();
            BeanUtils.copyProperties(bid, bidDTO);

            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
            autoGraphResponse.setStatus(0);
            autoGraphResponse.setStatusMessage("Your bid has been submitted.");
            autoGraphResponse.setResponseData(bidDTO);

            return autoGraphResponse;
        }

        throw new AppException("This casual job is not receiving anymore bids.");

    }


    public AutoGraphResponse cancelBid(User user, Long bidId) throws AppException {
        Bid bid = this.bidRepository.getById(bidId);

        if(!bid.getBidStatus().equals(BidStatus.ACTIVE))
            throw new AppException("This bid can not be cancelled because this bid is not currently active.");

        if(bid!=null && bid.getBidSubmittedByUserId().equals(user.getUserId()))
        {
            CasualJob casualJob = this.casualJobRepository.getById(bid.getCasualJobId());
            if(casualJob!=null && !casualJob.getCasualJobStatus().equals(CasualJobStatus.COMPLETED))
            {
                bid.setBidStatus(BidStatus.WITHDRAWN);
                this.bidRepository.save(bid);

                AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
                autoGraphResponse.setStatus(0);
                autoGraphResponse.setStatusMessage("The selected bid has been cancelled.");

                return autoGraphResponse;
            }

            throw new AppException("Your bid can not be cancelled because the casual job is not open to receiving or updating bids.");
        }
        throw new AppException("This bid could not be found. Please try again.");
    }




    public AutoGraphResponse selectWinningBid(User user, SelectWinningBidRequest selectWinningBidRequest) throws AppException {

        CasualJob casualJob = this.casualJobRepository.getById(selectWinningBidRequest.getCasualJobId());
        if(!(casualJob!=null && casualJob.getCasualJobStatus().equals(CasualJobStatus.OPEN)
                && casualJob.getSubmittedByUserId().equals(user.getUserId())))
        {
            throw new AppException("The selected bid can not be selected as the winning bid because the casual job was not posted by you.");
        }


        System.out.println(selectWinningBidRequest.getBidScheduleWonDTOList().size());
        int totalBidScheduleWon = selectWinningBidRequest.getBidScheduleWonDTOList().stream().mapToInt(bsw -> {

            Collection<Bid> bidList = this.bidRepository.getBidByBidIdsAndCasualJobId(bsw.getBidIdList(), casualJob.getCasualJobId());
            int totalBids = 0;
            if(bidList!=null && bidList.size()==bsw.getBidIdList().size())
            {
                CasualJobSchedule casualJobSchedule = this.casualJobScheduleRepository.getById(bsw.getCasualJobScheduleId());
                totalBids = bidList.stream().mapToInt(bl -> {
                    ArrayList newList = new ArrayList();
                    newList.add(casualJobSchedule.getCasualJobScheduleId());
                    System.out.println(newList);;
                    List<CasualJobScheduleBid> casualJobScheduleBidList = this.casualJobScheduleBidRepository.
                            getCasualJobScheduleBidByScheduleIdAndBidId(bl.getBidId(), newList);
                    System.out.println(casualJobScheduleBidList.size());;
                    CasualJobScheduleBid casualJobScheduleBid = casualJobScheduleBidList.get(0);
                    casualJobScheduleBid.setIsWon(true);
                    this.casualJobScheduleBidRepository.save(casualJobScheduleBid);

                    bl.setBidStatus(BidStatus.WON);
                    this.bidRepository.save(bl);


                    Shift shift = new Shift();
                    shift.setEndTime(casualJobSchedule.getScheduleEndDate());
                    shift.setStartTime(casualJobSchedule.getScheduleStartDate());
                    shift.setBidId(bl.getBidId());
                    shift.setEmployeeUserId(bl.getBidSubmittedByUserId());
                    shift.setUserEmployerId(casualJob.getSubmittedByUserEmployerId());
                    shift.setShiftStatus(ShiftStatus.PENDING);
                    shift.setCasualJobScheduleId(casualJobSchedule.getCasualJobScheduleId());
                    shift.setCasualJobId(casualJob.getCasualJobId());
                    this.shiftRepository.save(shift);

                    return 1;

                }).sum();
            }
            return totalBids;
        }).sum();

        if(totalBidScheduleWon>0)
        {
            casualJob.setCasualJobStatus(CasualJobStatus.ASSIGNED_WINNERS);
            this.casualJobRepository.save(casualJob);

            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
            autoGraphResponse.setStatus(0);
            autoGraphResponse.setStatusMessage("Your selected bidder(s) have been assigned to the job");

            return autoGraphResponse;
        }

        throw new AppException("We could not specify the selected bid(s) as the winning bids.");
    }

    public AutoGraphResponse viewBidByJobId(User user, Long jobId) {
        Collection<Bid> bidList = this.bidRepository.getBidByCasualJobId(jobId);
        AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
        autoGraphResponse.setStatus(0);
        autoGraphResponse.setStatusMessage("List of bids for the selected casual job.");
        autoGraphResponse.setResponseData(bidList);

        return autoGraphResponse;
    }

    public AutoGraphResponse updateABid(User user, UpdateABidRequest updateABidRequest) throws AppException {
        final Bid bid = this.bidRepository.getById(updateABidRequest.getBidId());
        Collection<Bid> bidList = this.bidRepository.getBidByCasualJobId(updateABidRequest.getCasualJobId());
        if(bidList!=null)
        {
            bidList.stream().reduce(bl -> bl.getBidId().equals(bid.getBidId()))
        }
        CasualJob casualJob = this.casualJobRepository.getById(updateABidRequest.getCasualJobId());
        UserEmployer userEmployer = this.userEmployerRepository
                .getUserEmployerByUserEmployerId(casualJob.getSubmittedByUserEmployerId());
        List<CasualJobScheduleBid> casualJobScheduleBidList = this.casualJobScheduleBidRepository.
                getCasualJobScheduleBidByBidId(updateABidRequest.getBidId());
        if(casualJobScheduleBidList!=null)
        {
            casualJobScheduleBidList.stream().map(cjs -> {
                this.casualJobScheduleBidRepository.delete(cjs);
                return cjs;
            }).collect(Collectors.toList());
        }

        if(casualJob!=null && casualJob.getCasualJobStatus().equals(CasualJobStatus.OPEN))
        {

            Collection<CasualJobSchedule> casualJobSchedules =
                    this.casualJobScheduleRepository.getCasualJobScheduleByCasualJobScheduleId(
                            updateABidRequest.getBidScheduleIdList()
                    );
            List<Long> casualJobScheduleIds = casualJobSchedules.stream().
                    map(cjs -> cjs.getCasualJobScheduleId()).
                    collect(Collectors.toList());

            if(bid==null)
            {
                bid = new Bid();
            }
            else
            {
                Collection<CasualJobScheduleBid> casualJobScheduleBids = this.casualJobScheduleBidRepository
                        .getCasualJobScheduleBidByScheduleIdAndBidId(
                                bid.getBidId(),
                                casualJobScheduleIds

                        );
                casualJobScheduleBids.stream().map(cjs -> {
                    this.casualJobScheduleBidRepository.delete(cjs);
                    return null;
                }).collect(Collectors.toList());
            }
            bid.setBidAmountPerHour(updateABidRequest.getBidAmountPerHour());
            bid.setBidDetails(updateABidRequest.getBidDetails());
            bid.setBidStatus(BidStatus.ACTIVE);
            bid.setCasualJobId(casualJob.getCasualJobId());
            bid.setBidSubmittedByUserId(user.getUserId());
            Bid bidCreated = (Bid) this.bidRepository.save(bid);






            System.out.println(casualJobSchedules.size());
            casualJobSchedules.stream().map(bs -> {
                CasualJobScheduleBid casualJobScheduleBid = new CasualJobScheduleBid();
                casualJobScheduleBid.setBidOwnerId(bidCreated.getBidSubmittedByUserId());
                casualJobScheduleBid.setJobId(bidCreated.getCasualJobId());
                casualJobScheduleBid.setJobScheduleId(bs.getCasualJobScheduleId());
                casualJobScheduleBid.setBidId(bidCreated.getBidId());
                casualJobScheduleBid = (CasualJobScheduleBid)this.casualJobScheduleBidRepository.save(casualJobScheduleBid);

                return casualJobScheduleBid;
            }).collect(Collectors.toList());


            BidDTO bidDTO = new BidDTO();
            BeanUtils.copyProperties(bid, bidDTO);

            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
            autoGraphResponse.setStatus(0);
            autoGraphResponse.setStatusMessage("Your bid has been submitted.");
            autoGraphResponse.setResponseData(bidDTO);

            return autoGraphResponse;
        }

        throw new AppException("This casual job is not receiving anymore bids.");
    }
}
