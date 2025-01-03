package com.choco_tur.choco_tur.web;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.choco_tur.choco_tur.data.Business;
import com.choco_tur.choco_tur.service.BusinessService;
import com.choco_tur.choco_tur.service.JwtService;
import com.choco_tur.choco_tur.service.UserAlreadyExistAuthenticationException;
import com.choco_tur.choco_tur.utils.CommonUtils;
import com.choco_tur.choco_tur.web.dto.BusinessRegistrationDto;
import com.choco_tur.choco_tur.web.dto.ChangePasswordDto;
import com.choco_tur.choco_tur.web.dto.InvitationValidationDto;
import com.choco_tur.choco_tur.web.dto.LoginDto;
import com.choco_tur.choco_tur.web.dto.UserLoginWithTokenDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/business")
public class BusinessController {

    private final BusinessService businessService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final MessageSource messageSource;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public BusinessController(
        BusinessService businessService,
        ApplicationEventPublisher applicationEventPublisher,
        MessageSource messageSource,
        JwtService jwtService,
        AuthenticationManager authenticationManager
    ) {
        this.businessService = businessService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.messageSource = messageSource;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/validateInvitation")
    public ResponseEntity<String> validateInvitation(
            @Valid @RequestBody InvitationValidationDto invitationValidationDto,
            HttpServletRequest request
    ) throws ExecutionException, InterruptedException, UserAlreadyExistAuthenticationException {
        if (!jwtService.isTokenValid(invitationValidationDto.getInvitationToken(), invitationValidationDto.getEmail())) {
            return new ResponseEntity<>("Token expired or invalid", HttpStatus.UNAUTHORIZED);
        }

        if (businessService.getBusinessByEmail(invitationValidationDto.getEmail()) != null) {
            return new ResponseEntity<>("A business with email " + invitationValidationDto.getEmail() + " already exists!", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/registration")
    public ResponseEntity<String> registerBusiness(
            @Valid @RequestBody BusinessRegistrationDto businessRegistrationDto,
            HttpServletRequest request
    ) throws ExecutionException, InterruptedException, UserAlreadyExistAuthenticationException {
        if (!jwtService.isTokenValid(businessRegistrationDto.getInvitationToken(), businessRegistrationDto.getEmail())) {
            return new ResponseEntity<>("Token expired or invalid", HttpStatus.UNAUTHORIZED);
        }

        if (businessService.getBusinessByEmail(businessRegistrationDto.getEmail()) != null) {
            return new ResponseEntity<>("A business with email " + businessRegistrationDto.getEmail() + " already exists!", HttpStatus.UNAUTHORIZED);
        }

        Business business = businessService.registerNewBusiness(businessRegistrationDto);

        String appUrl = request.getContextPath();
        String number = CommonUtils.getSixDigitNumberSequence();
        applicationEventPublisher.publishEvent(new OnBusinessRegistrationCompleteEvent(business,
                request.getLocale(), appUrl, number));

        return new ResponseEntity<>(number, HttpStatus.OK);
    }

    @GetMapping("/registrationConfirmation")
    public ResponseEntity<?> confirmRegistration(
            @RequestParam("email")String email,
            @RequestParam("number")String number
    ) throws ExecutionException, InterruptedException {
        Business business = businessService.getBusinessByEmail(email);
        if (business == null) {
            return new ResponseEntity<>("No business found by email", HttpStatus.BAD_REQUEST);
        }

        if (!number.equals(business.getEmailVerificationNumber())) {
            return new ResponseEntity<>("Authentication number is wrong", HttpStatus.BAD_REQUEST);
        }

        Calendar calendar = Calendar.getInstance();
        if (business.getEmailVerificationNumberExpirationTime() - calendar.getTime().getTime() <= 0) {
            return new ResponseEntity<>("Authentication number expired", HttpStatus.BAD_REQUEST);
        }

        business.setEmailValidationStatus(true);
        business.setEmailVerificationNumber(null);
        business.setEmailVerificationNumberExpirationTime(-1);
        businessService.saveBusiness(business);

        String deviceRegistrationToken = business.getDeviceRegistrationToken();
        String jwtAccessToken = jwtService.generateAccessToken(email);
        String jwtRefreshToken = jwtService.generateRefreshToken(email);
        BusinessLoginResponse loginResponse = BusinessLoginResponse.builder()
                .deviceRegistrationToken(deviceRegistrationToken)
                .accessToken(jwtAccessToken)
                .accessTokenExpiresIn(jwtService.getAccessTokenExpirationTime())
                .refreshToken(jwtRefreshToken)
                .refreshTokenExpiresIn(jwtService.getRefreshTokenExpirationTime())
                .build();

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/resendEmailVerificationNumber")
    public ResponseEntity<String> resendEmailVerificationNumber(
            @RequestParam("email")String email,
            @RequestParam("number")String number,
            HttpServletRequest request
    ) throws ExecutionException, InterruptedException {
        Business business = businessService.getBusinessByEmail(email);
        if (business == null) {
            return new ResponseEntity<>("No business found with email " + email, HttpStatus.BAD_REQUEST);
        }
        if (!business.getEmailVerificationNumber().equals(number)) {
            return new ResponseEntity<>("Business with email " + email + " has different verification number", HttpStatus.BAD_REQUEST);
        }

        String appUrl = request.getContextPath();
        String newNumber = CommonUtils.getSixDigitNumberSequence();
        applicationEventPublisher.publishEvent(new OnBusinessRegistrationCompleteEvent(business,
                request.getLocale(), appUrl, newNumber));

        return new ResponseEntity<>(number, HttpStatus.OK);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(
        @RequestBody String email,
        HttpServletRequest request
    ) throws ExecutionException, InterruptedException {
        Business business = businessService.getBusinessByEmail(email);
        if (business == null) {
            return new ResponseEntity<>("No business found with email " + email, HttpStatus.BAD_REQUEST);
        }

        String number = CommonUtils.getSixDigitNumberSequence();
        businessService.savePasswordResetNumber(business, number);
        businessService.sendPasswordResetNumber(business.getEmail(), number, request.getLocale());

        return new ResponseEntity<>("Password reset number sent", HttpStatus.OK);
    }

    @GetMapping("/resetPasswordTest")
    public ResponseEntity<String> changePassword(
        @RequestParam("email")String email,
        @RequestParam("number")String number
    ) throws ExecutionException, InterruptedException {
        Business business = businessService.getBusinessByEmail(email);
        if (business == null) {
            return new ResponseEntity<>("No business found with email " + email, HttpStatus.BAD_REQUEST);
        }
        if (!business.getPasswordResetNumber().equals(number)) {
            return new ResponseEntity<>("Business with email " + email + " has different verification number", HttpStatus.BAD_REQUEST);
        }
        Calendar calendar = Calendar.getInstance();
        if (business.getPasswordResetNumberGenerationTime() - calendar.getTime().getTime() <= 0) {
            return new ResponseEntity<>("Password reset number expired", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("OK, proceed", HttpStatus.OK);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> savePassword(
            @Valid @RequestBody ChangePasswordDto changePasswordDto
    ) throws ExecutionException, InterruptedException {
        Business business = businessService.getBusinessByEmail(changePasswordDto.getEmail());
        if (business == null) {
            return new ResponseEntity<>("No business found with email " + changePasswordDto.getEmail(), HttpStatus.BAD_REQUEST);
        }
        if (!business.getPasswordResetNumber().equals(changePasswordDto.getPasswordRecoveryNumber())) {
            return new ResponseEntity<>("Business with email " + changePasswordDto.getEmail() + " has different password reset number", HttpStatus.BAD_REQUEST);
        }

        Calendar calendar = Calendar.getInstance();
        if (business.getPasswordResetNumberGenerationTime() - calendar.getTime().getTime() <= 0) {
            return new ResponseEntity<>("Password reset number expired", HttpStatus.BAD_REQUEST);
        }

        String encodedPassword = businessService.encodePassword(changePasswordDto.getPassword());
        business.setPassword(encodedPassword);
        business.setPasswordResetNumber(null);
        business.setPasswordResetNumberGenerationTime(-1);
        businessService.saveBusiness(business);

        return new ResponseEntity<>("Password reset!", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto businessLoginDto) throws ExecutionException, InterruptedException {
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        businessLoginDto.getEmail(), businessLoginDto.getPassword());
        this.authenticationManager.authenticate(authenticationRequest);
        // TODO: Handle DisabledException, LockedException

        Business business = businessService.getBusinessByEmail(businessLoginDto.getEmail());
        if (business == null) {
            return new ResponseEntity<>("Business " + businessLoginDto.getEmail() + " not found!", HttpStatus.BAD_REQUEST);
        }

        String deviceRegistrationToken = business.getDeviceRegistrationToken();
        String jwtAccessToken = jwtService.generateAccessToken(businessLoginDto.getEmail());
        String jwtRefreshToken = jwtService.generateRefreshToken(businessLoginDto.getEmail());
        BusinessLoginResponse loginResponse = BusinessLoginResponse.builder()
                .deviceRegistrationToken(deviceRegistrationToken)
                .accessToken(jwtAccessToken)
                .accessTokenExpiresIn(jwtService.getAccessTokenExpirationTime())
                .refreshToken(jwtRefreshToken)
                .refreshTokenExpiresIn(jwtService.getRefreshTokenExpirationTime())
                .build();

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/loginWithToken")
    public ResponseEntity<?> loginWithToken(@RequestBody UserLoginWithTokenDto userLoginWithTokenDto) throws ExecutionException, InterruptedException {
        if (!jwtService.isTokenValid(userLoginWithTokenDto.getAccessToken(), userLoginWithTokenDto.getEmail())) {
            return new ResponseEntity<>("Token expired", HttpStatus.UNAUTHORIZED);
        }

        Business business = businessService.getBusinessByEmail(userLoginWithTokenDto.getEmail());

        String jwtRefreshToken = jwtService.generateRefreshToken(userLoginWithTokenDto.getEmail());
        UserLoginResponse loginResponse = UserLoginResponse.builder()
                .accessToken(userLoginWithTokenDto.getAccessToken())
                .accessTokenExpiresIn(jwtService.getAccessTokenExpirationTime())
                .refreshToken(jwtRefreshToken)
                .refreshTokenExpiresIn(jwtService.getRefreshTokenExpirationTime())
                .build();

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(
            @RequestParam("email")String email,
            @RequestParam("refreshToken")String refreshToken
    ) {
        if (!jwtService.isTokenValid(refreshToken, email)) {
            return new ResponseEntity<>("Refresh token expired.", HttpStatus.UNAUTHORIZED);
        }

        // Regenerate new tokens.
        String jwtAccessToken = jwtService.generateAccessToken(email);
        String jwtRefreshToken = jwtService.generateRefreshToken(email);
        UserLoginResponse loginResponse = UserLoginResponse.builder()
                .accessToken(jwtAccessToken)
                .accessTokenExpiresIn(jwtService.getAccessTokenExpirationTime())
                .refreshToken(jwtRefreshToken)
                .refreshTokenExpiresIn(jwtService.getRefreshTokenExpirationTime())
                .build();

        return ResponseEntity.ok(loginResponse);
    }
}
