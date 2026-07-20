package com.syncstate.go.cardinal.inside.ShiftKrew.services;


import com.syncstate.go.cardinal.inside.ShiftKrew.enums.Role;
import com.syncstate.go.cardinal.inside.ShiftKrew.enums.UserStatus;
import com.syncstate.go.cardinal.inside.ShiftKrew.exceptions.AppException;
import com.syncstate.go.cardinal.inside.ShiftKrew.exceptions.InstanceExistsException;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.*;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.dto.UserDTO;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.dto.UserDataDTO;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.dto.UserEmployerDTO;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.AddUserTechnicalTrainingRequest;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.CreateNewUserAccountRequest;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.AddUserSkillRequest;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.AddUserWorkExperienceRequest;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.responses.AutoGraphResponse;
import com.syncstate.go.cardinal.inside.ShiftKrew.repositories.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserReferralRepository userReferralRepository;

    @Autowired
    UserSkillRepository userSkillSetRepository;

    @Autowired
    UserWorkExperienceRepository userWorkExperienceRepository;

    @Autowired
    UserWorkExperienceSkillSetRepository userWorkExperienceSkillSetRepository;

    @Autowired
    UserTechnicalTrainingRepository userTechnicalTrainingRepository;

    @Autowired
    UserEmployerRepository userEmployerRepository;



    @Autowired
    private PasswordEncoder passwordEncoder;

    public AutoGraphResponse getAllUsers(){
        Collection<UserDTO> allUsers = userRepository.getAllUsers();

        AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
        autoGraphResponse.setStatus(0);
        autoGraphResponse.setStatusMessage("Users listed");
        Map<String, Collection> responseData = new HashMap<>();
        responseData.put("userList", allUsers);
        autoGraphResponse.setResponseData(responseData);

        return autoGraphResponse;
    }

    public AutoGraphResponse getUserById(Long userId) {
        UserDTO user = userRepository.getUserById(userId);

        AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
        autoGraphResponse.setStatus(0);
        autoGraphResponse.setStatusMessage("User details");
        autoGraphResponse.setResponseData(user);

        return autoGraphResponse;
    }

    public AutoGraphResponse createNewUserAccount(CreateNewUserAccountRequest createNewUserAccountRequest) {


        Optional<User> userOptional = this.userRepository.getPrimaryUserByUsername(createNewUserAccountRequest.getEmailAddress());
        if(!userOptional.isEmpty())
        {
            throw new InstanceExistsException("This email address has already been used.");
        }

        User user = new User();
        user.setUserStatus(UserStatus.SIGNED_UP);
        user.setPassword(passwordEncoder.encode(createNewUserAccountRequest.getPassword()));
        user.setFirstName(createNewUserAccountRequest.getFirstName());
        user.setLastName(createNewUserAccountRequest.getLastName());
        user.setUsername(createNewUserAccountRequest.getEmailAddress());
        user.setUserRole(Role.valueOf(createNewUserAccountRequest.getRole()));
        user = (User) userRepository.save(user);

        if(createNewUserAccountRequest.getReferralCode()!=null)
        {
            UserReferral userReferral = new UserReferral();
            userReferral.setUserId(user.getUserId());
            userReferral.setReferralCode(createNewUserAccountRequest.getReferralCode());
            userReferral.setIsValid(true);
            userReferral = (UserReferral) userReferralRepository.save(userReferral);
        }

        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        userDTO.setRole(user.getUserRole().value);

        AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
        autoGraphResponse.setStatus(0);
        autoGraphResponse.setStatusMessage("Your ShiftKrew account has been setup successfully for you.");
        autoGraphResponse.setResponseData(userDTO);

        return autoGraphResponse;
    }

    public User getUserByUsernameForLogin(String username) throws Exception {
        Optional<User> userOptional =  this.userRepository.getAnyserByUsername(username);
        return userOptional.stream().findFirst().orElseThrow(
                () -> new Exception("Login failed. Provide valid username to login")
        );
    }

    public AutoGraphResponse addUserSkillset(User user, AddUserSkillRequest updateUserSkillSetRequest) throws AppException {

        List<UserSkill> skillCreated = updateUserSkillSetRequest.getSkillSetList().stream().map(skill -> {
            UserSkill userSkill = new UserSkill();
            userSkill.setUserId(user.getUserId());
            userSkill.setIsValid(true);
            userSkill.setSkillId(skill.getSkillId());
            userSkill.setSkillExpertiseLevel(skill.getExpertiseLevel());
            userSkill = (UserSkill) userSkillSetRepository.save(userSkill);
            return userSkill;
        }).collect(Collectors.toList());


        if(skillCreated!=null && skillCreated.size()>0)
        {
            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
            autoGraphResponse.setStatus(0);
            autoGraphResponse.setStatusMessage("Your profile has been updated to reflect your skillset.");
            autoGraphResponse.setResponseData(skillCreated);

            return autoGraphResponse;
        }

        throw new AppException("Your selected skills could not be added to your profile.");
    }

    public AutoGraphResponse addUserWorkExperience(User user, AddUserWorkExperienceRequest updateUserWorkExperienceRequest) throws AppException {
        UserWorkExperience userWorkExperience = new UserWorkExperience();
        userWorkExperience.setUserId(user.getUserId());
        userWorkExperience.setWorkExperienceDetails(updateUserWorkExperienceRequest.getWorkExperienceDetails());
        userWorkExperience.setEndDate(updateUserWorkExperienceRequest.getEndDate());
        userWorkExperience.setStartDate(updateUserWorkExperienceRequest.getStartDate());
        UserWorkExperience uwe = (UserWorkExperience) userWorkExperienceRepository.save(userWorkExperience);

        List<UserWorkExperienceSkill> userWorkExperienceSkillList = updateUserWorkExperienceRequest.getUserSkillList().stream().map(userSkillId -> {
            UserWorkExperienceSkill userWorkExperienceSkill = new UserWorkExperienceSkill();
            userWorkExperienceSkill.setUserWorkExperienceId(uwe.getUserWorkExperienceId());
            userWorkExperienceSkill.setUserSkillId(userSkillId);
            userWorkExperienceSkill = (UserWorkExperienceSkill) this.userWorkExperienceSkillSetRepository.save(userWorkExperienceSkill);
            return userWorkExperienceSkill;
        }).collect(Collectors.toList());

        if(userWorkExperienceSkillList!=null && userWorkExperienceSkillList.size()>0)
        {
            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
            autoGraphResponse.setStatus(0);
            autoGraphResponse.setStatusMessage("Your work experience has been added to your profile.");
            autoGraphResponse.setResponseData(userWorkExperienceSkillList);

            return autoGraphResponse;
        }

        throw new AppException("Your work experience could not be added to your profile.");
    }

    public AutoGraphResponse addUserTechnicalTraining(User user, AddUserTechnicalTrainingRequest addUserTechnicalTrainingRequest) throws AppException {
        List userTechnicalTrainings = addUserTechnicalTrainingRequest.getUserTechnicalTrainingList().stream().map(tt -> {

            UserTechnicalTraining userTechnicalTraining = new UserTechnicalTraining();
            if(tt.getUserTechnicalTrainingID()!=null)
            {
                userTechnicalTraining = userTechnicalTrainingRepository.getById(tt.getUserTechnicalTrainingID());
            }

            userTechnicalTraining.setUserId(user.getUserId());
            userTechnicalTraining.setTechnicalTrainingDetails(tt.getTechnicalTrainingDetails());
            userTechnicalTraining.setDateObtained(tt.getDateObtained());
            userTechnicalTraining.setDateExpires(tt.getDateExpires());
            userTechnicalTraining = (UserTechnicalTraining) userTechnicalTrainingRepository.save(userTechnicalTraining);
            return userTechnicalTraining;
        }).collect(Collectors.toList());

        if(userTechnicalTrainings!=null && userTechnicalTrainings.size()>0)
        {
            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
            autoGraphResponse.setStatus(0);
            autoGraphResponse.setStatusMessage("Your trainings have been added to your profile.");
            autoGraphResponse.setResponseData(userTechnicalTrainings);

            return autoGraphResponse;
        }

        throw new AppException("Your work experience could not be added to your profile.");
    }

    public AutoGraphResponse getUserData(User user) {
        List<UserEmployer> userEmployerList = this.userEmployerRepository.getUserEmployerByUserId(user.getUserId());
        UserDataDTO userDataDTO = new UserDataDTO();
        List<UserEmployerDTO> userEmployerDTOList = userEmployerList.stream().map(ue -> {
            UserEmployerDTO ued = new UserEmployerDTO();
            BeanUtils.copyProperties(ue, ued);
            ued.setUserEmployerID(ue.getUserEmployerId());
            return ued;
        }).collect(Collectors.toList());
        userDataDTO.setEmployerDataList(userEmployerList!=null && userEmployerList.size()>0 ? userEmployerDTOList : null);
        userDataDTO.setUsername(user.getUsername());
        userDataDTO.setFirstName(user.getFirstName());
        userDataDTO.setLastName(user.getLastName());
        userDataDTO.setStatus(user.getUserStatus().name());

        AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
        autoGraphResponse.setStatus(0);
        autoGraphResponse.setStatusMessage("Data summary for current user");
        autoGraphResponse.setResponseData(userDataDTO);

        return autoGraphResponse;
    }

    public ResponseEntity activationUserProfile(String activationCode) throws AppException {
        User user = this.userRepository.getUserByActivationCode(activationCode);
        if(user==null)
        {
            throw new AppException("Invalid action. Use the activation link to activate your profile.");
        }

        if(user!=null && user.getActivationCode()==null)
        {
            throw new AppException("Invalid action. This activation link is invalid.");
        }

        user.setActivationCode(null);
        user.setUserStatus(UserStatus.ACTIVATED);
        this.userRepository.save(user);

        AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
        autoGraphResponse.setStatus(0);
        autoGraphResponse.setStatusMessage("Your profile has been activated. You can now log in.");
        autoGraphResponse.setResponseData(null);

        return ResponseEntity.ok(autoGraphResponse);
    }

//    public AutoGraphResponse loginCustomer(LoginRequest loginRequest) {
//        try {
//            final Authentication authentication = authenticationManager.authenticate(
//                    //                new AuthenticationManagerCustom(loginUser.getEmailAddress(), loginUser.getPassword())
//                    //                new PayAccessAuthenticationProvider()
//
//                    new UsernamePasswordAuthenticationToken(
//                            loginRequest.getEmailAddress(),
//                            loginRequest.getPassword()
//                    )
//            );
//
//            System.out.println(authentication.isAuthenticated());
//            //logger.info("{}", authentication.isAuthenticated());
//            //logger.info("{}", authentication.getPrincipal());
//            //        logger.info("{}>>>>", loginUser.getEmailAddress());
//
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            final String token = "";//jwtTokenUtil.generateToken(authentication);
//            //logger.info("token...{}", token);
//
//
//
//
//            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
//            autoGraphResponse.setStatus(0);
//            autoGraphResponse.setStatusMessage("Login successful.");
//            autoGraphResponse.setResponseData(token);
//
//            return autoGraphResponse;
//        }
//        catch(ProviderNotFoundException //| JsonProcessingException
//                e)
//        {
//
//
//            AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
//            autoGraphResponse.setStatus(0);
//            autoGraphResponse.setStatusMessage("Invalid username/password combination. Please provide a valid username/password to log in");
//            autoGraphResponse.setResponseData(null);
//
//            return autoGraphResponse;
//        }
//
//    }
}
