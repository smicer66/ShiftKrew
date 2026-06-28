package com.syncstate.go.cardinal.inside.ShiftKrew.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.syncstate.go.cardinal.inside.ShiftKrew.enums.Permission;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.User;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.responses.AuthResponse;
import com.syncstate.go.cardinal.inside.ShiftKrew.services.TokenService;
import com.syncstate.go.cardinal.inside.ShiftKrew.services.UserService;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@Component
public class SmartBankingAuthenticationProvider implements AuthenticationProvider {

    public SmartBankingAuthenticationProvider() {
        super();
    }
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserService userService;

    @Value("${authentication.provider.url}")
    private String authenticationProviderUrl;

    @Value("${token.period.in.mins}")
    int tokenPeriodInMins;

    @Value("${token.otp.period.in.mins}")
    int tokenOtpPeriodInMins;

    @SneakyThrows
    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {

//        JavaTimeModule javaTimeModule = new JavaTimeModule();
//        javaTimeModule.addDeserializer(LocalDateTime.class, new TimestampDeserializer());
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
//                .registerModule(javaTimeModule)
                .registerModule(new JavaTimeModule());



        String username = authentication.getPrincipal().toString(); // (1)

        logger.info("{} ... {}", username, authentication);
        String password = authentication.getCredentials().toString(); // (1)

        logger.info("{} ... {}", username, password);


//        Considering moving this bit of code to another component of the system that handles Token

        String subject = username;
        Permission permission = Permission.CREATE_NEW_ACQUIRER;
        AuthResponse jweAuth = tokenService.getToken(username, password, userService, Arrays.asList(permission), tokenPeriodInMins, null);

        JSONObject jsonObject = new JSONObject();
        if(jweAuth!=null && jweAuth.getValid().equals(Boolean.FALSE))
        {
            return null;
        }
        //List<Long> merchantIdList = userService.getMerchantIdsByUsername(requestData.getUsername());
        User us = userService.getUserByUsernameForLogin(subject);
//        ObjectMapper objectMapper = new ObjectMapper();
        //String merchantIds = objectMapper.writeValueAsString(merchantIdList);
//        String json = ("{\"subject\":\"" + subject
//                + "\",\"token\":\"" + jwe + "\",\"merchantList\":}");

        String jwe = jweAuth.getToken();

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + us.getUserRole().name()));
        UsernamePasswordAuthenticationToken r = new UsernamePasswordAuthenticationToken(us, password, authorities); // (4)
        System.out.println("r subject..." + r.getPrincipal());
        return r;
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
//
//        JSONObject personJsonObject = new JSONObject();
//        personJsonObject.put("username", username);
//        personJsonObject.put("password", password);
//        String req = new ObjectMapper().writeValueAsString(personJsonObject);
//
//        HttpEntity<String> entity =
//                new HttpEntity<String>(req, headers);
//
//        String uri = UriComponentsBuilder
//                .fromUriString(authenticationProviderUrl)
//                .build()
//                .toString();
//
//
//
//
//
//        try
//        {
//            ResponseEntity<SmartBankingAuthResponse> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, entity, SmartBankingAuthResponse.class);
//            if(responseEntity.getStatusCode().equals(HttpStatus.OK))
//            {
//                SmartBankingAuthResponse payAccessAuthResponse = responseEntity.getBody();
//
//                if(payAccessAuthResponse!=null && payAccessAuthResponse.getStatus().intValue()==1)
//                {
//                    throw new ApplicationException(payAccessAuthResponse.getMessage());
//                }
//                logger.info("{}", payAccessAuthResponse);
//
//                UserRole userRole = payAccessAuthResponse.getRole();
//                String subj = payAccessAuthResponse.getSubject();
//
//                logger.info("subj .. {}", subj);
//
//                logger.info("userRole .. {}", userRole);
//                User user = objectMapper.readValue(subj, User.class);
//                logger.info("use id .. {}", user.getUserId());
//
//                Set<SimpleGrantedAuthority> authorities = new HashSet<>();
//                authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
//
//
//                UsernamePasswordAuthenticationToken r = new UsernamePasswordAuthenticationToken(user, password, authorities); // (4)
//                logger.info("{}", r);
//                logger.info("{}", r.isAuthenticated());
//
//                return r;
//            }
//            else if(responseEntity.getStatusCode().equals(HttpStatus.UNAUTHORIZED))
//            {
//                throw new ApplicationException("Invalid credentials");
//            }
//
////            responseEntity.g
//
//        }
//        catch(HttpServerErrorException e)
//        {
//            logger.info("HttpServerErrorException ... {}", e.getResponseBodyAsString());
//            throw new Exception("Connection to authentication server timed out");
//
//        } catch (HttpClientErrorException e) {
//            logger.info("{}", e.getResponseBodyAsString());
//            throw new Exception("Connection to authentication server timed out");
//        } catch (ResourceAccessException e) {
//            logger.info("ResourceAccessException ... {}", e.getMessage());
//            throw new TimeoutException("Connection to resource timed out");
//        }
//
//        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        logger.info("{}", authentication.getCanonicalName());
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
