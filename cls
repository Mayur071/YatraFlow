[1mdiff --git a/yatraflow-backend/pom.xml b/yatraflow-backend/pom.xml[m
[1mindex 58e9e31..2728726 100644[m
[1m--- a/yatraflow-backend/pom.xml[m
[1m+++ b/yatraflow-backend/pom.xml[m
[36m@@ -80,6 +80,12 @@[m
 			<artifactId>spring-boot-starter-mail</artifactId>[m
 		</dependency>[m
 [m
[32m+[m
[32m+[m		[32m<dependency>[m
[32m+[m			[32m<groupId>org.springframework.boot</groupId>[m
[32m+[m			[32m<artifactId>spring-boot-starter-thymeleaf</artifactId>[m
[32m+[m		[32m</dependency>[m
[32m+[m
 		<dependency>[m
 			<groupId>org.postgresql</groupId>[m
 			<artifactId>postgresql</artifactId>[m
[1mdiff --git a/yatraflow-backend/src/main/java/com/yatraflow/auth/controller/AuthController.java b/yatraflow-backend/src/main/java/com/yatraflow/auth/controller/AuthController.java[m
[1mindex d9f91f4..979639c 100644[m
[1m--- a/yatraflow-backend/src/main/java/com/yatraflow/auth/controller/AuthController.java[m
[1m+++ b/yatraflow-backend/src/main/java/com/yatraflow/auth/controller/AuthController.java[m
[36m@@ -2,10 +2,12 @@[m [mpackage com.yatraflow.auth.controller;[m
 [m
 import com.yatraflow.auth.dto.request.LoginRequest;[m
 import com.yatraflow.auth.dto.request.RegisterRequest;[m
[32m+[m[32mimport com.yatraflow.auth.dto.response.EmailVerificationResponse;[m
 import com.yatraflow.auth.dto.response.LoginResponse;[m
 import com.yatraflow.auth.dto.response.RegisterResponse;[m
 import com.yatraflow.auth.services.login.LoginResult;[m
 import com.yatraflow.auth.services.login.LoginService;[m
[32m+[m[32mimport com.yatraflow.auth.services.register.EmailVerificationService;[m
 import com.yatraflow.auth.services.register.RegisterService;[m
 import com.yatraflow.exception.ForbiddenException;[m
 import com.yatraflow.exception.UnauthorizedException;[m
[36m@@ -40,6 +42,8 @@[m [mpublic class AuthController {[m
     private final JwtProperties jwtProperties;[m
     private final UserService userService;[m
 [m
[32m+[m[32m    private final EmailVerificationService emailVerificationService;[m
[32m+[m
     @PostMapping("/register")[m
     public ResponseEntity<RegisterResponse> register([m
             @Valid @RequestBody RegisterRequest request[m
[36m@@ -236,4 +240,20 @@[m [mpublic class AuthController {[m
                 Map.of("token", csrfToken.getToken())[m
         );[m
     }[m
[32m+[m
[32m+[m[32m    @GetMapping("/verify-email")[m
[32m+[m[32m    public ResponseEntity<EmailVerificationResponse> verifyEmail([m
[32m+[m[32m            @RequestParam("token") String token[m
[32m+[m[32m    ){[m
[32m+[m[32m        emailVerificationService.verifyEmail(token);[m
[32m+[m
[32m+[m[32m        return ResponseEntity.ok([m
[32m+[m[32m                new EmailVerificationResponse([m
[32m+[m[32m                        "Email verified successfully. Your YatraFlow account is now active."[m
[32m+[m[32m        )[m
[32m+[m[32m        );[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m
[32m+[m
 }[m
[1mdiff --git a/yatraflow-backend/src/main/java/com/yatraflow/auth/dto/response/RegisterResponse.java b/yatraflow-backend/src/main/java/com/yatraflow/auth/dto/response/RegisterResponse.java[m
[1mindex 23d7c39..f963352 100644[m
[1m--- a/yatraflow-backend/src/main/java/com/yatraflow/auth/dto/response/RegisterResponse.java[m
[1m+++ b/yatraflow-backend/src/main/java/com/yatraflow/auth/dto/response/RegisterResponse.java[m
[36m@@ -5,15 +5,10 @@[m [mimport lombok.*;[m
 @Builder[m
 public record RegisterResponse([m
 [m
[31m-       Long id,[m
[32m+[m[32m        String message,[m
 [m
[31m-       String firstName,[m
[32m+[m[32m        String email[m
 [m
[31m-       String lastName,[m
[31m-[m
[31m-       String email,[m
[31m-[m
[31m-       String phoneNumber[m
 [m
 ){[m
 [m
[1mdiff --git a/yatraflow-backend/src/main/java/com/yatraflow/auth/mapper/AuthMapper.java b/yatraflow-backend/src/main/java/com/yatraflow/auth/mapper/AuthMapper.java[m
[1mindex cc35ae6..acc1c90 100644[m
[1m--- a/yatraflow-backend/src/main/java/com/yatraflow/auth/mapper/AuthMapper.java[m
[1m+++ b/yatraflow-backend/src/main/java/com/yatraflow/auth/mapper/AuthMapper.java[m
[36m@@ -2,6 +2,7 @@[m [mpackage com.yatraflow.auth.mapper;[m
 [m
 import com.yatraflow.auth.dto.request.RegisterRequest;[m
 import com.yatraflow.auth.dto.response.RegisterResponse;[m
[32m+[m[32mimport com.yatraflow.auth.entity.PendingRegistration;[m
 import com.yatraflow.user.entity.User;[m
 import org.mapstruct.Mapper;[m
 import org.mapstruct.Mapping;[m
[36m@@ -13,6 +14,7 @@[m [mimport org.mapstruct.ReportingPolicy;[m
 )[m
 public interface AuthMapper {[m
 [m
[32m+[m[32m    // RegisterRequest -> User[m
     @Mapping(target = "id", ignore = true)[m
     @Mapping(target = "enabled", ignore = true)[m
     @Mapping(target = "emailVerified", ignore = true)[m
[36m@@ -20,9 +22,23 @@[m [mpublic interface AuthMapper {[m
     @Mapping(target = "createdAt", ignore = true)[m
     @Mapping(target = "updatedAt", ignore = true)[m
     @Mapping(target = "roles", ignore = true)[m
[31m-[m
     User toUser(RegisterRequest request);[m
 [m
[32m+[m
[32m+[m[32m    // PendingRegistration -> User[m
[32m+[m[32m    @Mapping(target = "id", ignore = true)[m
[32m+[m[32m    @Mapping(target = "password", source = "passwordHash")[m
[32m+[m[32m    @Mapping(target = "enabled", constant = "true")[m
[32m+[m[32m    @Mapping(target = "emailVerified", constant = "true")[m
[32m+[m[32m    @Mapping(target = "accountLocked", constant = "false")[m
[32m+[m[32m    @Mapping(target = "createdAt", ignore = true)[m
[32m+[m[32m    @Mapping(target = "updatedAt", ignore = true)[m
[32m+[m[32m    @Mapping(target = "roles", ignore = true)[m
[32m+[m
[32m+[m
[32m+[m[32m    User toUser(PendingRegistration pendingRegistration);[m
[32m+[m
[32m+[m
     RegisterResponse toRegisterResponse(User user);[m
 [m
 }[m
[1mdiff --git a/yatraflow-backend/src/main/java/com/yatraflow/auth/notification/EmailService.java b/yatraflow-backend/src/main/java/com/yatraflow/auth/notification/EmailService.java[m
[1mindex b79f42b..e7d5191 100644[m
[1m--- a/yatraflow-backend/src/main/java/com/yatraflow/auth/notification/EmailService.java[m
[1m+++ b/yatraflow-backend/src/main/java/com/yatraflow/auth/notification/EmailService.java[m
[36m@@ -2,5 +2,5 @@[m [mpackage com.yatraflow.auth.notification;[m
 [m
 public interface EmailService {[m
 [m
[31m-    void sendEmail(String to, String subject, String body);[m
[32m+[m[32m    void sendEmail(String to, String subject, String htmlContent);[m
 }[m
[1mdiff --git a/yatraflow-backend/src/main/java/com/yatraflow/auth/notification/EmailServiceImpl.java b/yatraflow-backend/src/main/java/com/yatraflow/auth/notification/EmailServiceImpl.java[m
[1mindex 8342803..32774c7 100644[m
[1m--- a/yatraflow-backend/src/main/java/com/yatraflow/auth/notification/EmailServiceImpl.java[m
[1m+++ b/yatraflow-backend/src/main/java/com/yatraflow/auth/notification/EmailServiceImpl.java[m
[36m@@ -6,7 +6,6 @@[m [mimport jakarta.mail.internet.MimeMessage;[m
 import lombok.RequiredArgsConstructor;[m
 import lombok.extern.slf4j.Slf4j;[m
 import org.springframework.mail.MailException;[m
[31m-import org.springframework.mail.SimpleMailMessage;[m
 import org.springframework.mail.javamail.JavaMailSender;[m
 import org.springframework.mail.javamail.MimeMessageHelper;[m
 import org.springframework.stereotype.Service;[m
[36m@@ -14,16 +13,12 @@[m [mimport org.springframework.stereotype.Service;[m
 @Service[m
 @RequiredArgsConstructor[m
 @Slf4j[m
[31m-public class EmailServiceImpl implements EmailService {[m
[32m+[m[32mpublic class EmailServiceImpl implements EmailService  {[m
 [m
     private final JavaMailSender mailSender;[m
 [m
     @Override[m
[31m-    public void sendHtmlEmail([m
[31m-            String to,[m
[31m-            String subject,[m
[31m-            String htmlContent[m
[31m-    ) {[m
[32m+[m[32m    public void sendEmail(String to, String subject, String htmlContent) {[m
 [m
         try {[m
 [m
[1mdiff --git a/yatraflow-backend/src/main/java/com/yatraflow/auth/repository/EmailVerificationTokenRepository.java b/yatraflow-backend/src/main/java/com/yatraflow/auth/repository/EmailVerificationTokenRepository.java[m
[1mindex 660a4e4..a8e457d 100644[m
[1m--- a/yatraflow-backend/src/main/java/com/yatraflow/auth/repository/EmailVerificationTokenRepository.java[m
[1m+++ b/yatraflow-backend/src/main/java/com/yatraflow/auth/repository/EmailVerificationTokenRepository.java[m
[36m@@ -11,5 +11,7 @@[m [mpublic interface EmailVerificationTokenRepository extends JpaRepository<EmailVer[m
 [m
     void deleteByPendingRegistrationId(Long pendingRegistrationId);[m
 [m
[32m+[m[32m    Optional<EmailVerificationToken> findByPendingRegistrationId(Long pendingRegistrationId);[m
[32m+[m
 [m
 }[m
[1mdiff --git a/yatraflow-backend/src/main/java/com/yatraflow/auth/services/email/EmailVerificationTokenService.java b/yatraflow-backend/src/main/java/com/yatraflow/auth/services/email/EmailVerificationTokenService.java[m
[1mindex 14c4315..b7e5cfe 100644[m
[1m--- a/yatraflow-backend/src/main/java/com/yatraflow/auth/services/email/EmailVerificationTokenService.java[m
[1m+++ b/yatraflow-backend/src/main/java/com/yatraflow/auth/services/email/EmailVerificationTokenService.java[m
[36m@@ -3,6 +3,8 @@[m [mpackage com.yatraflow.auth.services.email;[m
 import com.yatraflow.auth.entity.EmailVerificationToken;[m
 import com.yatraflow.auth.entity.PendingRegistration;[m
 [m
[32m+[m[32mimport java.util.Optional;[m
[32m+[m
 public interface EmailVerificationTokenService {[m
 [m
     String createVerificationToken(PendingRegistration pendingRegistration);[m
[36m@@ -12,4 +14,8 @@[m [mpublic interface EmailVerificationTokenService {[m
     void markTokenUsed(EmailVerificationToken token);[m
 [m
     void deleteToken(PendingRegistration pendingRegistration);[m
[32m+[m
[32m+[m[32m    Optional<EmailVerificationToken> getByPendingRegistration([m
[32m+[m[32m            PendingRegistration pendingRegistration[m
[32m+[m[32m    );[m
 }[m
[1mdiff --git a/yatraflow-backend/src/main/java/com/yatraflow/auth/services/email/EmailVerificationTokenServiceImpl.java b/yatraflow-backend/src/main/java/com/yatraflow/auth/services/email/EmailVerificationTokenServiceImpl.java[m
[1mindex 7832df7..160cc7c 100644[m
[1m--- a/yatraflow-backend/src/main/java/com/yatraflow/auth/services/email/EmailVerificationTokenServiceImpl.java[m
[1m+++ b/yatraflow-backend/src/main/java/com/yatraflow/auth/services/email/EmailVerificationTokenServiceImpl.java[m
[36m@@ -13,6 +13,8 @@[m [mimport java.security.NoSuchAlgorithmException;[m
 import java.security.SecureRandom;[m
 import java.time.LocalDateTime;[m
 import java.util.Base64;[m
[32m+[m[32mimport java.util.Optional;[m
[32m+[m
 import org.springframework.transaction.annotation.Transactional;[m
 [m
 @Service[m
[36m@@ -78,6 +80,11 @@[m [mpublic class EmailVerificationTokenServiceImpl implements EmailVerificationToken[m
 [m
     }[m
 [m
[32m+[m[32m    @Override[m
[32m+[m[32m    public Optional<EmailVerificationToken> getByPendingRegistration(PendingRegistration pendingRegistration) {[m
[32m+[m[32m        return tokenRepository.findByPendingRegistrationId(pendingRegistration.getId());[m
[32m+[m[32m    }[m
[32m+[m
     private String generateSecureToken() {[m
 [m
         byte[]  randomBytes = new byte[32];[m
[1mdiff --git a/yatraflow-backend/src/main/java/com/yatraflow/auth/services/email/PendingRegistrationService.java b/yatraflow-backend/src/main/java/com/yatraflow/auth/services/email/PendingRegistrationService.java[m
[1mindex 9966c08..bc1d23d 100644[m
[1m--- a/yatraflow-backend/src/main/java/com/yatraflow/auth/services/email/PendingRegistrationService.java[m
[1m+++ b/yatraflow-backend/src/main/java/com/yatraflow/auth/services/email/PendingRegistrationService.java[m
[36m@@ -15,4 +15,9 @@[m [mpublic interface PendingRegistrationService {[m
     PendingRegistration getByEmail(String email);[m
 [m
     void deleteExpiredRegistrations();[m
[32m+[m
[32m+[m[32m    boolean existsByEmail(String email);[m
[32m+[m
[32m+[m[32m    void delete(PendingRegistration pendingRegistration);[m
[32m+[m
 }[m
[1mdiff --git a/yatraflow-backend/src/main/java/com/yatraflow/auth/services/email/PendingRegistrationServiceImpl.java b/yatraflow-backend/src/main/java/com/yatraflow/auth/services/email/PendingRegistrationServiceImpl.java[m
[1mindex 23ec0a9..142eeff 100644[m
[1m--- a/yatraflow-backend/src/main/java/com/yatraflow/auth/services/email/PendingRegistrationServiceImpl.java[m
[1m+++ b/yatraflow-backend/src/main/java/com/yatraflow/auth/services/email/PendingRegistrationServiceImpl.java[m
[36m@@ -4,6 +4,7 @@[m [mimport com.yatraflow.auth.entity.PendingRegistration;[m
 import com.yatraflow.auth.repository.PendingRegistrationRepository;[m
 import com.yatraflow.exception.ResourceNotFoundException;[m
 import lombok.RequiredArgsConstructor;[m
[32m+[m[32mimport lombok.extern.slf4j.Slf4j;[m
 import org.springframework.stereotype.Service;[m
 import org.springframework.transaction.annotation.Transactional;[m
 import java.time.LocalDateTime;[m
[36m@@ -11,6 +12,7 @@[m [mimport java.time.LocalDateTime;[m
 @Service[m
 @Transactional[m
 @RequiredArgsConstructor[m
[32m+[m[32m@Slf4j[m
 public class PendingRegistrationServiceImpl implements PendingRegistrationService{[m
 [m
     private final PendingRegistrationRepository pendingRegistrationRepository;[m
[36m@@ -48,4 +50,23 @@[m [mpublic class PendingRegistrationServiceImpl implements PendingRegistrationServic[m
 [m
         pendingRegistrationRepository.deleteByExpiresAtBefore(LocalDateTime.now());[m
     }[m
[32m+[m
[32m+[m
[32m+[m[32m    @Override[m
[32m+[m[32m    @Transactional(readOnly = true)[m
[32m+[m[32m    public boolean existsByEmail(String email) {[m
[32m+[m[32m        return pendingRegistrationRepository.existsByEmail(email);[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    @Override[m
[32m+[m[32m    public void delete(PendingRegistration pendingRegistration) {[m
[32m+[m
[32m+[m[32m        if(pendingRegistration == null){[m
[32m+[m[32m            throw new IllegalArgumentException("Pending registration cannot be null");[m
[32m+[m[32m        }[m
[32m+[m
[32m+[m[32m        pendingRegistrationRepository.delete(pendingRegistration);[m
[32m+[m[32m        log.debug("Pending registration deleted | email={}", pendingRegistration.getEmail());[m
[32m+[m
[32m+[m[32m    }[m
 }[m
[1mdiff --git a/yatraflow-backend/src/main/java/com/yatraflow/auth/services/register/EmailVerificationService.java b/yatraflow-backend/src/main/java/com/yatraflow/auth/services/register/EmailVerificationService.java[m
[1mindex eb8cd1e..7eeb722 100644[m
[1m--- a/yatraflow-backend/src/main/java/com/yatraflow/auth/services/register/EmailVerificationService.java[m
[1m+++ b/yatraflow-backend/src/main/java/com/yatraflow/auth/services/register/EmailVerificationService.java[m
[36m@@ -7,4 +7,8 @@[m [mpublic interface EmailVerificationService {[m
     void createAndSendVerificationEmail(PendingRegistration pendingRegistration);[m
 [m
     void verifyEmail(String rawToken);[m
[32m+[m
[32m+[m[32m    void resendVerificationEmail(String email);[m
[32m+[m
[32m+[m
 }[m
[1mdiff --git a/yatraflow-backend/src/main/java/com/yatraflow/auth/services/register/EmailVerificationServiceImpl.java b/yatraflow-backend/src/main/java/com/yatraflow/auth/services/register/EmailVerificationServiceImpl.java[m
[1mindex 1a5bd96..69bf5ec 100644[m
[1m--- a/yatraflow-backend/src/main/java/com/yatraflow/auth/services/register/EmailVerificationServiceImpl.java[m
[1m+++ b/yatraflow-backend/src/main/java/com/yatraflow/auth/services/register/EmailVerificationServiceImpl.java[m
[36m@@ -1,16 +1,376 @@[m
 package com.yatraflow.auth.services.register;[m
 [m
[32m+[m[32mimport com.yatraflow.auth.entity.EmailVerificationToken;[m
 import com.yatraflow.auth.entity.PendingRegistration;[m
[32m+[m[32mimport com.yatraflow.auth.mapper.AuthMapper;[m
[32m+[m[32mimport com.yatraflow.auth.notification.EmailService;[m
[32m+[m[32mimport com.yatraflow.auth.services.email.EmailVerificationTokenService;[m
[32m+[m[32mimport com.yatraflow.auth.services.email.PendingRegistrationService;[m
[32m+[m[32mimport com.yatraflow.exception.BusinessException;[m
[32m+[m[32mimport com.yatraflow.exception.ResourceAlreadyExistsException;[m
[32m+[m[32mimport com.yatraflow.role.entity.Role;[m
[32m+[m[32mimport com.yatraflow.role.entity.RoleName;[m
[32m+[m[32mimport com.yatraflow.role.service.RoleService;[m
[32m+[m[32mimport com.yatraflow.user.entity.User;[m
[32m+[m[32mimport com.yatraflow.user.service.UserService;[m
[32m+[m[32mimport jakarta.transaction.Transactional;[m
[32m+[m[32mimport lombok.RequiredArgsConstructor;[m
[32m+[m[32mimport lombok.extern.slf4j.Slf4j;[m
[32m+[m[32mimport org.springframework.beans.factory.annotation.Value;[m
[32m+[m[32mimport org.springframework.stereotype.Service;[m
[32m+[m[32mimport org.springframework.web.util.UriComponentsBuilder;[m
[32m+[m[32mimport org.thymeleaf.TemplateEngine;[m
[32m+[m[32mimport org.thymeleaf.context.Context;[m
[32m+[m[32mimport org.thymeleaf.exceptions.TemplateProcessingException;[m
 [m
[32m+[m[32mimport java.time.LocalDateTime;[m
[32m+[m[32mimport java.util.Optional;[m
[32m+[m
[32m+[m[32m@Transactional[m
[32m+[m[32m@Service[m
[32m+[m[32m@Slf4j[m
[32m+[m[32m@RequiredArgsConstructor[m
 public class EmailVerificationServiceImpl implements EmailVerificationService  {[m
 [m
[32m+[m
[32m+[m[32m    private static final String VERIFICATION_POINT = "/api/v1/auth/verify-email";[m
[32m+[m
[32m+[m[32m    private final EmailVerificationTokenService tokenService;[m
[32m+[m
[32m+[m[32m    private final EmailService emailService;[m
[32m+[m
[32m+[m[32m    private final AuthMapper authMapper;[m
[32m+[m
[32m+[m[32m    private final RoleService roleService;[m
[32m+[m
[32m+[m[32m    private final UserService userService;[m
[32m+[m
[32m+[m[32m    private final TemplateEngine templateEngine;[m
[32m+[m
[32m+[m[32m    private final PendingRegistrationService pendingRegistrationService;[m
[32m+[m
[32m+[m
[32m+[m
[32m+[m[32m    @Value("${app.email-verification.base-url:http://localhost:8080}")[m
[32m+[m[32m    private String baseUrl;[m
[32m+[m
[32m+[m[32m    @Value("${app.email-verification.expiry-minutes:15}")[m
[32m+[m[32m    private int verificationExpiryMinutes;[m
[32m+[m
[32m+[m[32m    // =========================================================[m
[32m+[m[32m    // CREATE + SEND VERIFICATION EMAIL[m
[32m+[m[32m    // =========================================================[m
[32m+[m
     @Override[m
     public void createAndSendVerificationEmail(PendingRegistration pendingRegistration) {[m
 [m
[32m+[m[32m        if(pendingRegistration == null){[m
[32m+[m[32m            throw new BusinessException("Pending Registration is required");[m
[32m+[m[32m        }[m
[32m+[m
[32m+[m[32m        String email = pendingRegistration.getEmail();[m
[32m+[m
[32m+[m[32m        log.info("Creating email verification request | email={}", email);[m
[32m+[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m[32m        // 1. Generate secure verification token[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m
[32m+[m[32m        String rawToken = tokenService.createVerificationToken(pendingRegistration);[m
[32m+[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m[32m        // 2. Build verification URL[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m
[32m+[m[32m        String verificationUrl = UriComponentsBuilder[m
[32m+[m[32m                .fromUriString(baseUrl)[m
[32m+[m[32m                .path(VERIFICATION_POINT)[m
[32m+[m[32m                .queryParam("token",rawToken)[m
[32m+[m[32m                .toUriString();[m
[32m+[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m[32m        // 3. Prepare Thymeleaf context[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m
[32m+[m
[32m+[m[32m        Context context = new Context();[m
[32m+[m
[32m+[m[32m        context.setVariable("firstName",pendingRegistration.getFirstName());[m
[32m+[m[32m        context.setVariable("verificationUrl",verificationUrl);[m
[32m+[m[32m        context.setVariable("expiryMinutes",verificationExpiryMinutes);[m
[32m+[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m[32m        // 4. Render HTML template[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m
[32m+[m[32m        final String htmlContent;[m
[32m+[m
[32m+[m[32m        try {[m
[32m+[m
[32m+[m[32m            htmlContent =[m
[32m+[m[32m                    templateEngine.process([m
[32m+[m[32m                            "email/email-verification",[m
[32m+[m[32m                            context[m
[32m+[m[32m                    );[m
[32m+[m
[32m+[m[32m        } catch ([m
[32m+[m[32m                TemplateProcessingException e[m
[32m+[m[32m        ) {[m
[32m+[m
[32m+[m[32m            log.error([m
[32m+[m[32m                    "Failed to render verification email template | email={}",[m
[32m+[m[32m                    email,[m
[32m+[m[32m                    e[m
[32m+[m[32m            );[m
[32m+[m
[32m+[m[32m            throw new BusinessException([m
[32m+[m[32m                    "Unable to prepare verification email. Please try again later."[m
[32m+[m[32m            );[m
[32m+[m[32m        }[m
[32m+[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m[32m        // 5. Send HTML email[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m
[32m+[m[32m        emailService.sendEmail([m
[32m+[m[32m                email,[m
[32m+[m[32m                "Verify your YatraFlow account",[m
[32m+[m[32m                htmlContent[m
[32m+[m[32m        );[m
[32m+[m
[32m+[m[32m        log.info([m
[32m+[m[32m                "Verification email initiated successfully | email={}",[m
[32m+[m[32m                email[m
[32m+[m[32m        );[m
     }[m
 [m
[32m+[m[32m    // =========================================================[m
[32m+[m[32m    // VERIFY EMAIL[m
[32m+[m[32m    // =========================================================[m
[32m+[m
     @Override[m
     public void verifyEmail(String rawToken) {[m
 [m
[32m+[m[32m        log.info("Email verification request received.");[m
[32m+[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m[32m        // 1. Validate input[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m
[32m+[m[32m        if (rawToken == null || rawToken.isBlank()) {[m
[32m+[m
[32m+[m[32m            throw new BusinessException([m
[32m+[m[32m                    "Verification token is required."[m
[32m+[m[32m            );[m
[32m+[m[32m        }[m
[32m+[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m[32m        // 2. Validate token[m
[32m+[m[32m        //[m
[32m+[m[32m        // TokenService handles:[m
[32m+[m[32m        // - SHA-256 hashing[m
[32m+[m[32m        // - token lookup[m
[32m+[m[32m        // - used-token validation[m
[32m+[m[32m        // - expiry validation[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m
[32m+[m[32m        EmailVerificationToken verificationToken =[m
[32m+[m[32m                tokenService.getValidateToken(rawToken);[m
[32m+[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m[32m        // 3. Get pending registration[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m
[32m+[m[32m        PendingRegistration pendingRegistration =[m
[32m+[m[32m                verificationToken.getPendingRegistration();[m
[32m+[m
[32m+[m[32m        if (pendingRegistration == null) {[m
[32m+[m
[32m+[m[32m            log.error([m
[32m+[m[32m                    "No pending registration associated with verification token."[m
[32m+[m[32m            );[m
[32m+[m
[32m+[m[32m            throw new BusinessException([m
[32m+[m[32m                    "Pending registration not found."[m
[32m+[m[32m            );[m
[32m+[m[32m        }[m
[32m+[m
[32m+[m[32m        String email = pendingRegistration.getEmail();[m
[32m+[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m[32m        // 4. Defensive duplicate check[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m
[32m+[m[32m        if (userService.existsByEmail(email)) {[m
[32m+[m
[32m+[m[32m            log.warn([m
[32m+[m[32m                    "Verification rejected. User already exists | email={}",[m
[32m+[m[32m                    email[m
[32m+[m[32m            );[m
[32m+[m
[32m+[m[32m            throw new ResourceAlreadyExistsException([m
[32m+[m[32m                    "Email already registered."[m
[32m+[m[32m            );[m
[32m+[m[32m        }[m
[32m+[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m[32m        // 5. Convert PendingRegistration -> User[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m
[32m+[m[32m        User user =[m
[32m+[m[32m                authMapper.toUser([m
[32m+[m[32m                        pendingRegistration[m
[32m+[m[32m                );[m
[32m+[m
[32m+[m[32m        if (user == null) {[m
[32m+[m
[32m+[m[32m            log.error([m
[32m+[m[32m                    "Failed to create User from PendingRegistration | email={}",[m
[32m+[m[32m                    email[m
[32m+[m[32m            );[m
[32m+[m
[32m+[m[32m            throw new BusinessException([m
[32m+[m[32m                    "Unable to complete registration."[m
[32m+[m[32m            );[m
[32m+[m[32m        }[m
[32m+[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m[32m        // 6. Assign default role[m
[32m+[m[32m        //[m
[32m+[m[32m        // RoleService is intentionally used here because the[m
[32m+[m[32m        // actual User is created only after email verification.[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m
[32m+[m[32m        Role userRole =[m
[32m+[m[32m                roleService.getRoleByName([m
[32m+[m[32m                        RoleName.ROLE_USER[m
[32m+[m[32m                );[m
[32m+[m
[32m+[m[32m        user.getRoles().add(userRole);[m
[32m+[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m[32m        // 7. Persist verified User[m
[32m+[m[32m        //[m
[32m+[m[32m        // AuthMapper sets emailVerified=true.[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m
[32m+[m[32m        User savedUser =[m
[32m+[m[32m                userService.createUser(user);[m
[32m+[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m[32m        // 8. Remove verification token[m
[32m+[m[32m        //[m
[32m+[m[32m        // Successful verification means the token must never[m
[32m+[m[32m        // be usable again.[m
[32m+[m[32m        // -----------------------------------------------------[m
[32m+[m
[32m+[m[32m        tokenService.deleteToken([m
[32m+[m[32m                pendingRegistration[m
[32m+[m[32m        );[m
[32m+[m
[32m+[m[32m        log.info([m
[32m+[m[32m                "Email verification completed successfully | email={} | userId={}",[m
[32m+[m[32m                savedUser.getEmail(),[m
[32m+[m[32m                savedUser.getId()[m
[32m+[m[32m        );[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    @Override[m
[32m+[m[32m    @Transactional[m
[32m+[m[32m    public void resendVerificationEmail(String email) {[m
[32m+[m
[32m+[m[32m        log.info([m
[32m+[m[32m                "Resend verification request received | email={}",[m
[32m+[m[32m                email[m
[32m+[m[32m        );[m
[32m+[m
[32m+[m[32m        if (email == null || email.isBlank()) {[m
[32m+[m[32m            throw new BusinessException([m
[32m+[m[32m                    "Email is required."[m
[32m+[m[32m            );[m
[32m+[m[32m        }[m
[32m+[m
[32m+[m[32m        String normalizedEmail =[m
[32m+[m[32m                email.trim().toLowerCase(java.util.Locale.ROOT);[m
[32m+[m
[32m+[m[32m        // ---------------------------------------------------------[m
[32m+[m[32m        // 1. Check whether user is already registered[m
[32m+[m[32m        // ---------------------------------------------------------[m
[32m+[m
[32m+[m[32m        if (userService.existsByEmail(normalizedEmail)) {[m
[32m+[m
[32m+[m[32m            log.warn([m
[32m+[m[32m                    "Resend verification rejected | user already registered | email={}",[m
[32m+[m[32m                    normalizedEmail[m
[32m+[m[32m            );[m
[32m+[m
[32m+[m[32m            throw new BusinessException([m
[32m+[m[32m                    "Email is already verified."[m
[32m+[m[32m            );[m
[32m+[m[32m        }[m
[32m+[m
[32m+[m[32m        // ---------------------------------------------------------[m
[32m+[m[32m        // 2. Find pending registration[m
[32m+[m[32m        // ---------------------------------------------------------[m
[32m+[m
[32m+[m[32m        PendingRegistration pendingRegistration =[m
[32m+[m[32m                pendingRegistrationService.getByEmail([m
[32m+[m[32m                        normalizedEmail[m
[32m+[m[32m                );[m
[32m+[m
[32m+[m[32m        // ---------------------------------------------------------[m
[32m+[m[32m        // 3. Find existing verification token[m
[32m+[m[32m        // ---------------------------------------------------------[m
[32m+[m
[32m+[m[32m       Optional<EmailVerificationToken> existingToken =[m
[32m+[m[32m                tokenService.getByPendingRegistration([m
[32m+[m[32m                        pendingRegistration[m
[32m+[m[32m                );[m
[32m+[m
[32m+[m[32m        // ---------------------------------------------------------[m
[32m+[m[32m        // 4. If an existing token is still valid, don't issue[m
[32m+[m[32m        //    another one.[m
[32m+[m[32m        // ---------------------------------------------------------[m
[32m+[m
[32m+[m[32m        if (existingToken.isPresent()) {[m
[32m+[m
[32m+[m[32m            EmailVerificationToken token =[m
[32m+[m[32m                    existingToken.get();[m
[32m+[m
[32m+[m[32m            if (token.getExpiresAt().isAfter(LocalDateTime.now())) {[m
[32m+[m
[32m+[m[32m                log.warn([m
[32m+[m[32m                        "Resend verification rejected | existing token still valid | email={}",[m
[32m+[m[32m                        normalizedEmail[m
[32m+[m[32m                );[m
[32m+[m
[32m+[m[32m                throw new BusinessException([m
[32m+[m[32m                        "Your current verification link is still valid."[m
[32m+[m[32m                );[m
[32m+[m[32m            }[m
[32m+[m
[32m+[m[32m            // Existing token has expired → remove it.[m
[32m+[m[32m            tokenService.deleteToken([m
[32m+[m[32m                    pendingRegistration[m
[32m+[m[32m            );[m
[32m+[m[32m        }[m
[32m+[m
[32m+[m[32m        // ---------------------------------------------------------[m
[32m+[m[32m        // 5. Create a fresh token and send a new verification email[m
[32m+[m[32m        // ---------------------------------------------------------[m
[32m+[m
[32m+[m[32m        createAndSendVerificationEmail([m
[32m+[m[32m                pendingRegistration[m
[32m+[m[32m        );[m
[32m+[m
[32m+[m[32m        log.info([m
[32m+[m[32m                "Verification email resent successfully | email={}",[m
[32m+[m[32m                normalizedEmail[m
[32m+[m[32m        );[m
     }[m
[32m+[m
[32m+[m
[32m+[m
 }[m
[41m+[m
[41m+[m
[1mdiff --git a/yatraflow-backend/src/main/java/com/yatraflow/auth/services/register/RegisterServiceImpl.java b/yatraflow-backend/src/main/java/com/yatraflow/auth/services/register/RegisterServiceImpl.java[m
[1mindex 3ef2001..12f78e2 100644[m
[1m--- a/yatraflow-backend/src/main/java/com/yatraflow/auth/services/register/RegisterServiceImpl.java[m
[1m+++ b/yatraflow-backend/src/main/java/com/yatraflow/auth/services/register/RegisterServiceImpl.java[m
[36m@@ -2,13 +2,10 @@[m [mpackage com.yatraflow.auth.services.register;[m
 [m
 import com.yatraflow.auth.dto.request.RegisterRequest;[m
 import com.yatraflow.auth.dto.response.RegisterResponse;[m
[31m-import com.yatraflow.auth.mapper.AuthMapper;[m
[32m+[m[32mimport com.yatraflow.auth.entity.PendingRegistration;[m
[32m+[m[32mimport com.yatraflow.auth.services.email.PendingRegistrationService;[m
 import com.yatraflow.exception.BusinessException;[m
 import com.yatraflow.exception.ResourceAlreadyExistsException;[m
[31m-import com.yatraflow.role.entity.Role;[m
[31m-import com.yatraflow.role.entity.RoleName;[m
[31m-import com.yatraflow.role.service.RoleService;[m
[31m-import com.yatraflow.user.entity.User;[m
 import com.yatraflow.user.service.UserService;[m
 import jakarta.transaction.Transactional;[m
 import lombok.RequiredArgsConstructor;[m
[36m@@ -16,98 +13,175 @@[m [mimport lombok.extern.slf4j.Slf4j;[m
 import org.springframework.security.crypto.password.PasswordEncoder;[m
 import org.springframework.stereotype.Service;[m
 [m
[32m+[m[32mimport java.util.Locale;[m
[32m+[m
 @Service[m
 @RequiredArgsConstructor[m
 @Slf4j[m
 @Transactional[m
 public class RegisterServiceImpl implements RegisterService {[m
 [m
[31m-[m
     private final UserService userService;[m
[31m-[m
[31m-    private final RoleService roleService;[m
[31m-[m
     private final PasswordEncoder passwordEncoder;[m
[31m-[m
[31m-    private final AuthMapper authMapper;[m
[32m+[m[32m    private final PendingRegistrationService pendingRegistrationService;[m
[32m+[m[32m    private final EmailVerificationService emailVerificationService;[m
 [m
 [m
     @Override[m
     public RegisterResponse register(RegisterRequest request) {[m
 [m
[31m-        log.info("Registration request received for email: {}", request.getEmail());[m
[32m+[m[32m        String email = normalizeEmail(request.getEmail());[m
[32m+[m[32m        String phoneNumber = normalizePhoneNumber(request.getPhoneNumber());[m
[32m+[m
[32m+[m[32m        log.info([m
[32m+[m[32m                "Registration request received | email={}",[m
[32m+[m[32m                email[m
[32m+[m[32m        );[m
 [m
[31m-        validateEmail(request.getEmail());[m
[32m+[m[32m        // ---------------------------------------------------------[m
[32m+[m[32m        // Validation[m
[32m+[m[32m        // ---------------------------------------------------------[m
 [m
[31m-        validatePhoneNumber(request.getPhoneNumber());[m
[32m+[m[32m        validateEmail(email);[m
[32m+[m
[32m+[m[32m        validatePhoneNumber(phoneNumber);[m
 [m
         validatePassword([m
                 request.getPassword(),[m
                 request.getConfirmPassword()[m
         );[m
 [m
[31m-        User user = authMapper.toUser(request);[m
[32m+[m[32m        // ---------------------------------------------------------[m
[32m+[m[32m        // Password hashing[m
[32m+[m[32m        // ---------------------------------------------------------[m
[32m+[m
[32m+[m[32m        String passwordHash =[m
[32m+[m[32m                passwordEncoder.encode(request.getPassword());[m
[32m+[m
[32m+[m[32m        // ---------------------------------------------------------[m
[32m+[m[32m        // Create temporary registration[m
[32m+[m[32m        // ---------------------------------------------------------[m
 [m
[31m-        encodePassword(user);[m
[32m+[m[32m        PendingRegistration pendingRegistration =[m
[32m+[m[32m                pendingRegistrationService.createPendingRegistration([m
[32m+[m[32m                        email,[m
[32m+[m[32m                        passwordHash,[m
[32m+[m[32m                        request.getFirstName().trim(),[m
[32m+[m[32m                        request.getLastName().trim(),[m
[32m+[m[32m                        phoneNumber[m
[32m+[m[32m                );[m
 [m
[31m-        assignDefaultRole(user);[m
[32m+[m[32m        // ---------------------------------------------------------[m
[32m+[m[32m        // Create verification token + send verification email[m
[32m+[m[32m        // ---------------------------------------------------------[m
 [m
[31m-        User savedUser = userService.createUser(user);[m
[32m+[m[32m        emailVerificationService.createAndSendVerificationEmail([m
[32m+[m[32m                pendingRegistration[m
[32m+[m[32m        );[m
 [m
[31m-        log.info("User registered successfully with email: {}", savedUser.getEmail());[m
[32m+[m[32m        log.info([m
[32m+[m[32m                "Registration initiated successfully | email={}",[m
[32m+[m[32m                email[m
[32m+[m[32m        );[m
 [m
[31m-        return authMapper.toRegisterResponse(savedUser);[m
[32m+[m[32m        // User is NOT created yet.[m
[32m+[m[32m        return RegisterResponse.builder()[m
[32m+[m[32m                .message([m
[32m+[m[32m                        "Registration initiated. Please verify your email to complete registration."[m
[32m+[m[32m                )[m
[32m+[m[32m                .email(email)[m
[32m+[m[32m                .build();[m
     }[m
 [m
 [m
[31m-    private void validateEmail(String email){[m
[32m+[m[32m    // ---------------------------------------------------------[m
[32m+[m[32m    // Validation[m
[32m+[m[32m    // ---------------------------------------------------------[m
[32m+[m
[32m+[m[32m    private void validateEmail(String email) {[m
 [m
[31m-        if(userService.existsByEmail(email)){[m
[32m+[m[32m        // Already registered user[m
[32m+[m[32m        if (userService.existsByEmail(email)) {[m
 [m
[31m-            log.warn("Registration failed. Email already exists: {}",email);[m
[32m+[m[32m            log.warn([m
[32m+[m[32m                    "Registration rejected | reason=EMAIL_ALREADY_REGISTERED | email={}",[m
[32m+[m[32m                    email[m
[32m+[m[32m            );[m
 [m
[31m-            throw new ResourceAlreadyExistsException("Email already registered");[m
[32m+[m[32m            throw new ResourceAlreadyExistsException([m
[32m+[m[32m                    "Email already registered"[m
[32m+[m[32m            );[m
[32m+[m[32m        }[m
[32m+[m
[32m+[m[32m        // Verification already pending[m
[32m+[m[32m        if (pendingRegistrationService.existsByEmail(email)) {[m
[32m+[m
[32m+[m[32m            log.warn([m
[32m+[m[32m                    "Registration rejected | reason=EMAIL_VERIFICATION_PENDING | email={}",[m
[32m+[m[32m                    email[m
[32m+[m[32m            );[m
[32m+[m
[32m+[m[32m            throw new BusinessException([m
[32m+[m[32m                    "Email verification is already pending. Please verify your email."[m
[32m+[m[32m            );[m
         }[m
     }[m
 [m
[31m-    private void validatePhoneNumber(String phoneNumber){[m
 [m
[31m-        if(userService.existsByPhoneNumber(phoneNumber)){[m
[32m+[m[32m    private void validatePhoneNumber(String phoneNumber) {[m
[32m+[m
[32m+[m[32m        if (userService.existsByPhoneNumber(phoneNumber)) {[m
 [m
[31m-            log.warn("Registration failed. Phone number is already registered: {}", phoneNumber);[m
[32m+[m[32m            log.warn([m
[32m+[m[32m                    "Registration rejected | reason=PHONE_ALREADY_REGISTERED | phone={}",[m
[32m+[m[32m                    phoneNumber[m
[32m+[m[32m            );[m
 [m
[31m-            throw new ResourceAlreadyExistsException("phone number is already exists");[m
[32m+[m[32m            throw new ResourceAlreadyExistsException([m
[32m+[m[32m                    "Phone number is already registered"[m
[32m+[m[32m            );[m
         }[m
     }[m
 [m
[31m-    private void validatePassword(String password, String confirmPassword){[m
 [m
[31m-        if(!password.equals(confirmPassword)) {[m
[32m+[m[32m    private void validatePassword([m
[32m+[m[32m            String password,[m
[32m+[m[32m            String confirmPassword[m
[32m+[m[32m    ) {[m
 [m
[31m-            log.warn("Registration failed. password mismatch");[m
[32m+[m[32m        if (password == null || confirmPassword == null) {[m
 [m
[31m-            throw new BusinessException("\"Password and Confirm Password do not match.");[m
[32m+[m[32m            throw new BusinessException([m
[32m+[m[32m                    "Password and Confirm Password are required."[m
[32m+[m[32m            );[m
         }[m
 [m
[31m-    }[m
[31m-[m
[31m-    private void encodePassword(User user){[m
[32m+[m[32m        if (!password.equals(confirmPassword)) {[m
 [m
[31m-        user.setPassword([m
[31m-                passwordEncoder.encode(user.getPassword())[m
[31m-        );[m
[32m+[m[32m            log.warn([m
[32m+[m[32m                    "Registration rejected | reason=PASSWORD_MISMATCH"[m
[32m+[m[32m            );[m
 [m
[31m-        log.debug("Password encoded successfully.");[m
[32m+[m[32m            throw new BusinessException([m
[32m+[m[32m                    "Password and Confirm Password do not match."[m
[32m+[m[32m            );[m
[32m+[m[32m        }[m
     }[m
 [m
 [m
[31m-    private void assignDefaultRole(User user){[m
[32m+[m[32m    // ---------------------------------------------------------[m
[32m+[m[32m    // Normalization[m
[32m+[m[32m    // ---------------------------------------------------------[m
 [m
[31m-        Role role = roleService.getRoleByName(RoleName.ROLE_USER);[m
[31m-        user.getRoles().add(role);[m
[32m+[m[32m    private String normalizeEmail(String email) {[m
 [m
[31m-        log.debug("ROLE_USER assigned successfully");[m
[32m+[m[32m        return email.trim().toLowerCase(Locale.ROOT);[m
     }[m
 [m
[32m+[m
[32m+[m[32m    private String normalizePhoneNumber(String phoneNumber) {[m
[32m+[m
[32m+[m[32m        return phoneNumber.trim();[m
[32m+[m[32m    }[m
 }[m
 [m
[1mdiff --git a/yatraflow-backend/src/main/java/com/yatraflow/security/config/SecurityConfig.java b/yatraflow-backend/src/main/java/com/yatraflow/security/config/SecurityConfig.java[m
[1mindex 7d28cfe..8a09804 100644[m
[1m--- a/yatraflow-backend/src/main/java/com/yatraflow/security/config/SecurityConfig.java[m
[1m+++ b/yatraflow-backend/src/main/java/com/yatraflow/security/config/SecurityConfig.java[m
[36m@@ -68,7 +68,9 @@[m [mpublic class SecurityConfig {[m
                                 "/api/v1/auth/login",[m
                                 "/api/v1/auth/refresh-token",[m
                                 "/api/v1/auth/logout",[m
[31m-                                "/api/v1/test/**"[m
[32m+[m[32m                                "/api/v1/test/**",[m
[32m+[m[32m                                "/api/v1/auth/verify-email",[m
[32m+[m[32m                                "/resend-verification"[m
                         ).permitAll()[m
 [m
                         .requestMatchers([m
[1mdiff --git a/yatraflow-backend/src/main/resources/application.properties b/yatraflow-backend/src/main/resources/application.properties[m
[1mindex 73679dc..9cbdfc3 100644[m
[1m--- a/yatraflow-backend/src/main/resources/application.properties[m
[1m+++ b/yatraflow-backend/src/main/resources/application.properties[m
[36m@@ -43,4 +43,8 @@[m [mspring.mail.password=${MAIL_PASSWORD}[m
 [m
 spring.mail.properties.mail.smtp.auth=true[m
 spring.mail.properties.mail.smtp.starttls.enable=true[m
[31m-spring.mail.properties.mail.smtp.starttls.required=true[m
\ No newline at end of file[m
[32m+[m[32mspring.mail.properties.mail.smtp.starttls.required=true[m
[32m+[m
[32m+[m
[32m+[m[32mapp.email-verification.base-url=http://localhost:8080[m
[32m+[m[32mapp.email-verification.expiry-minutes=15[m
\ No newline at end of file[m
