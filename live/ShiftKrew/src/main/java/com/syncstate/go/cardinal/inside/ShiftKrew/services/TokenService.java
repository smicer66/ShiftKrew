package com.syncstate.go.cardinal.inside.ShiftKrew.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.X509CertUtils;
import com.nimbusds.jwt.*;
import com.syncstate.go.cardinal.inside.ShiftKrew.enums.Permission;
import com.syncstate.go.cardinal.inside.ShiftKrew.enums.TokenType;
import com.syncstate.go.cardinal.inside.ShiftKrew.exceptions.AppException;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.*;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.requests.ValidateTokenRequest;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.responses.AuthResponse;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.responses.AutoGraphResponse;
import com.syncstate.go.cardinal.inside.ShiftKrew.repositories.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

//
@Service
public class TokenService {

    private final Logger LOG = LoggerFactory.getLogger(TokenService.class);
    
    @Value("${token.issuer.url}")
    private String issuer;
    
    @Value("${token.issuer.pkcs12}")
    private String serverPKCS;
    
    @Value("${service.provider.x509}")
    private String clientCertificate;

    @Value("${token.issuer.pkcs12}")
    private String clientPKCS;

    @Value("${service.provider.x509}")
    private String serverCertificate;

    @Autowired
    private TokenRepository tokenRepository;


    private RSAKey getPublicKey(String certificateFile) {
        RSAKey publicKey = null;
        try {
            String fileContent = new String(Files.readAllBytes(Paths.get(certificateFile)));
            X509Certificate certificate = X509CertUtils.parse(fileContent);
            publicKey = RSAKey.parse(certificate);
            LOG.info("Public key was fetched from the certificate");
        } catch (IOException | JOSEException e) {
            LOG.error(e.toString());
        }
        return publicKey;
    }
    
    private RSAKey getJSONWebKey(String pkcsFile) {
        RSAKey jwk = null;
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream pkcs = new FileInputStream(pkcsFile);
            keyStore.load(pkcs, "".toCharArray());
            Enumeration aliases = keyStore.aliases();
            while(aliases.hasMoreElements()){
                String alias = (String)aliases.nextElement();
                RSAPrivateKey privateKey = (RSAPrivateKey) keyStore.getKey(alias,"".toCharArray());
                X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
                RSAKey publicKey = RSAKey.parse(certificate);
                jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
                break;
            }
        } catch (IOException 
                | KeyStoreException 
                | JOSEException 
                | NoSuchAlgorithmException 
                | UnrecoverableKeyException 
                | CertificateException ex) {
            LOG.error(ex.toString());
        }
        return jwk;
    }
    
    public AuthResponse getToken(String username, String password, UserService userService, List<Permission> permissions, int periodInMins, String key) throws JsonProcessingException {
        String token = "unknown";
        try {

            User us = userService.getUserByUsernameForLogin(username);

            if(us==null)
            {
                return new AuthResponse(false, "Invalid username/password combination. Please provide a valid username/password combination", null);
            }


            if(us.getUserStatus().equals("NOT_ACTIVATED"))
            {
                return new AuthResponse(false, "Activate your account before you can log in", null);
            }
            else if(us.getUserStatus().equals("SUSPENDED"))
            {
                return new AuthResponse(false, "Profile can not be logged in at this moment. Contact system administrator", null);
            }
            else if(us.getUserStatus().equals("DELETED"))
            {
                return new AuthResponse(false, "Invalid username/password combination. Please provide a valid username/password combination", null);
            }

//            String hh = BCrypt.hashpw("businessCategory", BCrypt.gensalt(12));
//            LOG.info("eeeee {}", hh);
//            LOG.info("bcrypted {} => {}", us.getPassword(), password);
            boolean matchedPin = BCrypt.checkpw(password, us.getPassword());

            if(matchedPin==false)
            {
                return new AuthResponse(false, "Invalid username/password combination. Please provide a valid username/password combination", null);
            }
            us.setPassword(null);
            String subject = username;


            LOG.info("{}", us.getPassword());
            LOG.info("user = " + username + ", passwprd = " + password);

            RSAKey serverJWK = getJSONWebKey(serverPKCS);
            
            JWSHeader jwtHeader = new JWSHeader.Builder(JWSAlgorithm.RS256).jwk(serverJWK).build();
            Calendar now = Calendar.getInstance();
            Date issueTime = now.getTime();
            now.add(Calendar.MINUTE, periodInMins);
            Date expiryTime = now.getTime();
            String jti = String.valueOf(issueTime.getTime());

            ObjectMapper mapper = new ObjectMapper();
            String usString = mapper.writeValueAsString(us);
           // Date expiryTime = issueTime.
            JWTClaimsSet.Builder jwtClaimsSetBuilder = new JWTClaimsSet.Builder()
                    .issuer(issuer)
                    .subject(subject)
                    .issueTime(issueTime)
                    .expirationTime(expiryTime)
                    .claim("user", usString)
                    .claim("permissions", permissions);

            if(key!=null)
            {
                jwtClaimsSetBuilder = jwtClaimsSetBuilder.claim("key", key);
            }

            JWTClaimsSet jwtClaims = jwtClaimsSetBuilder
                    .jwtID(jti)
                    .build();
            LOG.info("JWT claims = " + jwtClaims.toString());
            SignedJWT jws = new SignedJWT(jwtHeader, jwtClaims);
            RSASSASigner signer = new RSASSASigner(serverJWK);
            jws.sign(signer);
                        
             JWEHeader jweHeader = new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, 
                     EncryptionMethod.A256GCM).contentType("JWT").build();
            
            JWEObject jwe = new JWEObject(jweHeader, new Payload(jws));
            
            // Encrypt with the recipient's public key
            RSAKey clientPublicKey = getPublicKey(clientCertificate);

            jwe.encrypt(new RSAEncrypter(clientPublicKey));

            token = jwe.serialize();
            LOG.info("Token = " + token);
            
        } catch (final JOSEException e) {
            // TODO Auto-generated catch block
            LOG.error(e.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new AuthResponse(true, "Login successful", token);
    }




    public String getTokenByUsername(String username, UserService userService, List<Permission> permissions, int periodInMins, String key) throws JsonProcessingException {
        String token = null;
        try {

            User us = userService.getUserByUsernameForLogin(username);
            String subject = username;

            RSAKey serverJWK = getJSONWebKey(serverPKCS);

            JWSHeader jwtHeader = new JWSHeader.Builder(JWSAlgorithm.RS256).jwk(serverJWK).build();
            Calendar now = Calendar.getInstance();
            Date issueTime = now.getTime();
            now.add(Calendar.MINUTE, periodInMins);
            Date expiryTime = now.getTime();
            String jti = String.valueOf(issueTime.getTime());

            ObjectMapper mapper = new ObjectMapper();
            String usString = mapper.writeValueAsString(us);
            // Date expiryTime = issueTime.
            JWTClaimsSet.Builder jwtClaimsSetBuilder = new JWTClaimsSet.Builder()
                    .issuer(issuer)
                    .subject(subject)
                    .issueTime(issueTime)
                    .expirationTime(expiryTime)
                    .claim("user", usString)
                    .claim("permissions", permissions);

            if(key!=null)
            {
                jwtClaimsSetBuilder = jwtClaimsSetBuilder.claim("key", key);
            }

            JWTClaimsSet jwtClaims = jwtClaimsSetBuilder
                    .jwtID(jti)
                    .build();
            LOG.info("JWT claims = " + jwtClaims.toString());
            SignedJWT jws = new SignedJWT(jwtHeader, jwtClaims);
            RSASSASigner signer = new RSASSASigner(serverJWK);
            jws.sign(signer);

            JWEHeader jweHeader = new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256,
                    EncryptionMethod.A256GCM).contentType("JWT").build();

            JWEObject jwe = new JWEObject(jweHeader, new Payload(jws));

            // Encrypt with the recipient's public key
            RSAKey clientPublicKey = getPublicKey(clientCertificate);

            jwe.encrypt(new RSAEncrypter(clientPublicKey));

            token = jwe.serialize();
            LOG.info("Token = " + token);

        } catch (final JOSEException e) {
            // TODO Auto-generated catch block
            LOG.error(e.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return token;
    }

    public User getUserFromToken(HttpServletRequest request) throws JsonProcessingException {
        Enumeration<String> headers = request.getHeaderNames();
        String token = "unknown";
        User user = null;
        while(headers.hasMoreElements()) {
            String key = headers.nextElement();
            if(key.trim().equalsIgnoreCase("Authorization")) {
                String authorizationHeader = request.getHeader(key);
                if(!authorizationHeader.isEmpty()) {
                    String[] tokenData = authorizationHeader.split(" ");
                    if(tokenData.length == 2 && tokenData[0].trim().equalsIgnoreCase("Bearer")) {
                        token = tokenData[1];
                        LOG.info("Received token: " + token);
                        break;
                    }
                }
            }
        }

        try {
            LOG.info(">>>>>: " + 1);
            JWT jwt = JWTParser.parse(token);
            LOG.info(">>>>>: " + 2);
            LOG.info(jwt.getClass().getCanonicalName());
            if(jwt instanceof EncryptedJWT) {
                LOG.info(">>>>>: " + 3);
                EncryptedJWT jwe = (EncryptedJWT) jwt;
                LOG.info(">>>>>: " + 4);
                RSAKey clientJWK = getJSONWebKey(clientPKCS);
                LOG.info(">>>>>: " + 5);
                JWEDecrypter decrypter = new RSADecrypter(clientJWK);
                LOG.info(">>>>>: " + 6);
                jwe.decrypt(decrypter);
                LOG.info(">>>>>: " + 7);
                SignedJWT jws = jwe.getPayload().toSignedJWT();
                LOG.info(">>>>>: " + 8);

                RSAKey serverJWK = getPublicKey(serverCertificate);
                LOG.info(">>>>>: " + 9);
                RSASSAVerifier signVerifier = new RSASSAVerifier(serverJWK);
                LOG.info(">>>>>: " + 10);
                if(jws.verify(signVerifier)) {
                    JWTClaimsSet claims = jws.getJWTClaimsSet();
                    Date expiryTime = claims.getExpirationTime();
                    LOG.info("Expiry time = " + expiryTime.toString());
                    if(expiryTime.after(new Date())) {
                        Object userString = claims.getClaim("user");
                        String uString = String.valueOf(userString);
                        LOG.info("uString = " + uString);
                        ObjectMapper mapper = new ObjectMapper();
                        user = mapper.readValue(uString, User.class);
                        LOG.info("Token validated for user = " + uString);
                        LOG.info("Token validated for user = {}" + user);
                        return user;
                    }
                }
            }
            else if(jwt instanceof SignedJWT) {
                LOG.info(">>>>>: " + 3);
                SignedJWT jws = (SignedJWT) jwt;


                RSAKey serverJWK = getPublicKey(serverCertificate);
                LOG.info(">>>>>: " + 9);

                RSASSAVerifier signVerifier = new RSASSAVerifier(serverJWK);
                LOG.info(">>>>>: " + 10);

                JWTClaimsSet claims = jws.getJWTClaimsSet();
                Date expiryTime = claims.getExpirationTime();
                LOG.info("Expiry time = " + expiryTime.toString());
                if(expiryTime.after(new Date())) {
                    Object userString = claims.getClaim("user");
                    String uString = String.valueOf(userString);
                    LOG.info("uString = " + uString);
                    ObjectMapper mapper = new ObjectMapper();
                    user = mapper.readValue(uString, User.class);
                    LOG.info("Token validated for user = " + uString);
                    LOG.info("Token validated for user = {}" + user);
                    return user;
                }

            }
        }
        catch(ParseException | JOSEException ex) {
            LOG.error(ex.toString());
        }
        return null;
    }

    public ResponseEntity validateToken(ValidateTokenRequest validateTokenRequest) throws AppException {
        String tokenVal = validateTokenRequest.getToken();
        String data = validateTokenRequest.getData();
        String clientCode = validateTokenRequest.getClientCode();
        TokenType tokenType = validateTokenRequest.getTokenType();

        System.out.println("tokenVal" + tokenVal);

        Token token = (Token)tokenRepository.validateToken(tokenVal, data, tokenType);
        if(token!=null)
        {
            token.setUsedAt(LocalDateTime.now());
            tokenRepository.save(token);

            if(token.getTokenType().equals(TokenType.SIGNUP))
            {
//                BigInteger clientUserId = token.getTokenOwnedByEntityId();
//                ClientUser clientUser = (ClientUser)this.clientUserRepository.getById(clientUserId);
//                clientUser.setClientUserStatus(ClientUserStatus.ACTIVE);
//                clientUserRepository.save(clientUser);
//
//                ClientDomain clientDomain = this.clientDomainRepository.getActiveClientDomainByClientId(clientUser.getClientId());

                AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
                autoGraphResponse.setStatusMessage("Your SyncTracker account has been activated. Please login now to start using SyncTracker.");
                autoGraphResponse.setStatus(0);

//                Map<String, Object> map = new HashMap<>();
//                map.put("domain", clientDomain.getDomain());
//                if(clientDomain!=null)
//                    smartBankingResponse.setResponseObject(map);


                return ResponseEntity.ok().body(autoGraphResponse);

            }
        }
        throw new AppException("Validation was not successful. Please try again");
    }

}
