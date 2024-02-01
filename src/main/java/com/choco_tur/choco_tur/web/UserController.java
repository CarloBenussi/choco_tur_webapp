package com.choco_tur.choco_tur.web;

import com.choco_tur.choco_tur.data.UserLoginInfo;
import com.choco_tur.choco_tur.service.ExternalProviderService;
import com.choco_tur.choco_tur.service.JwtService;
import com.choco_tur.choco_tur.service.UserAlreadyExistAuthenticationException;
import com.choco_tur.choco_tur.utils.CommonUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.nimbusds.oauth2.sdk.GeneralException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.choco_tur.choco_tur.service.UserService;

import jakarta.validation.Valid;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final MessageSource messageSource;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final ExternalProviderService externalProviderService;

    public UserController(
            UserService userService,
            ApplicationEventPublisher applicationEventPublisher,
            MessageSource messageSource,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            ExternalProviderService externalProviderService
    ) {
        this.userService = userService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.messageSource = messageSource;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.externalProviderService = externalProviderService;
    }

    @PostMapping("/registration")
    public ResponseEntity<String> registerUser(
            @Valid @RequestBody UserRegistrationDto userDto,
            HttpServletRequest request
    ) {
        try {
            UserLoginInfo registered = userService.registerNewUser(userDto);

            String appUrl = request.getContextPath();
            applicationEventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered,
                    request.getLocale(), appUrl));
        } catch (UserAlreadyExistAuthenticationException uaeEx) {
            return new ResponseEntity<>(uaeEx.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String responseMessage = messageSource.getMessage("message.registrationSuccess",
                null, request.getLocale());
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PostMapping("/signInWithExtProvider")
    public ResponseEntity<Object> signInWithExtProvider(
        @Valid @RequestBody UserExtProviderSignInDto userDto,
        HttpServletRequest request
    ) throws GeneralSecurityException, IOException {
        try {
            externalProviderService.validateExtProviderToken(userDto);
            userService.signInUserWithExtProvider(userDto);
        } catch (RuntimeException | IOException | GeneralException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Authenticate user as well.
        // TODO: Here the 'authenticate' call will check that the hash of the passed password
        // matches the hash of the password saved for this user. So we need to generate a
        // temporary password for the user, save it (hashed) and return it on 'signInUserWithExtProvider'
        // so we can pass it here? Or maybe we can simply skip this step and generate the jwt token
        // directly?
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        userDto.getEmail(), null);
        this.authenticationManager.authenticate(authenticationRequest);
        // TODO: Handle DisabledException, LockedException, BadCredentialsException

        String jwtToken = jwtService.generateToken(userDto.getEmail());

        LoginResponse loginResponse = LoginResponse
                .builder().token(jwtToken).expiresIn(jwtService.getExpirationTime()).build();

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/registrationConfirmation")
    public ResponseEntity<String> confirmRegistration(
            @RequestParam("email")String email,
            @RequestParam("number")String number
    ) {
        UserLoginInfo user = userService.getUserByEmail(email);
        if (user == null) {
            return new ResponseEntity<>("No user found by email", HttpStatus.BAD_REQUEST);
        }

        if (!number.equals(user.getEmailVerificationNumber())) {
            return new ResponseEntity<>("Authentication number is wrong", HttpStatus.BAD_REQUEST);
        }

        Calendar calendar = Calendar.getInstance();
        if (user.getEmailVerificationNumberExpirationTime().getTime() - calendar.getTime().getTime() <= 0) {
            return new ResponseEntity<>("Authentication number expired", HttpStatus.BAD_REQUEST);
        }

        user.setEmailValidationStatus(true);
        user.setEmailVerificationNumber(null);
        user.setEmailVerificationNumberExpirationTime(null);
        userService.saveUser(user);

        return new ResponseEntity<>("Email confirmed!", HttpStatus.OK);
    }

    @GetMapping("/resendEmailVerificationNumber")
    public ResponseEntity<String> resendEmailVerificationNumber(
            @RequestParam("number")String number,
            HttpServletRequest request
    ) {
        UserLoginInfo user = userService.getUserByEmailVerificationNumber(number);
        if (user == null) {
            return new ResponseEntity<>("No user found by email verification token", HttpStatus.BAD_REQUEST);
        }

        String newNumber = CommonUtils.getSixDigitNumberSequence();
        userService.saveEmailVerificationNumber(user, newNumber);
        userService.sendEmailVerificationNumber(user, newNumber, request.getContextPath(), request.getLocale());

        return new ResponseEntity<>("Verification email resent", HttpStatus.OK);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(
        @RequestParam("email")String email,
        HttpServletRequest request
    ) {
        UserLoginInfo user = userService.getUserByEmail(email);
        if (user == null) {
            return new ResponseEntity<>("No user found by email " + email, HttpStatus.BAD_REQUEST);
        }

        String token = UUID.randomUUID().toString();
        userService.savePasswordResetToken(user, token);

        userService.sendPasswordResetToken(user, token, request.getContextPath(), request.getLocale());

        return new ResponseEntity<>("Password reset email resent", HttpStatus.OK);
    }

    @GetMapping("/changePassword")
    public ResponseEntity<String> changePassword(
        @RequestParam("token")String token
    ) {
        UserLoginInfo user = userService.getUserByPasswordResetToken(token);
        if (user == null) {
            return new ResponseEntity<>("No user found by password reset token", HttpStatus.BAD_REQUEST);
        }

        Calendar calendar = Calendar.getInstance();
        if (user.getPasswordResetTokenGenerationTime().getTime() - calendar.getTime().getTime() <= 0) {
            return new ResponseEntity<>("Password reset token expired", HttpStatus.BAD_REQUEST);
        }

        // TODO: Redirect to html page for password reset.
        return new ResponseEntity<>("Reset password please", HttpStatus.OK);
    }

    @PostMapping("/savePassword")
    public ResponseEntity<String> savePassword(
        @RequestParam("token")String token,
        @RequestParam("token")String newPassword
    ) {
        UserLoginInfo user = userService.getUserByPasswordResetToken(token);
        if (user == null) {
            return new ResponseEntity<>("No user found by password reset token", HttpStatus.BAD_REQUEST);
        }

        Calendar calendar = Calendar.getInstance();
        if (user.getPasswordResetTokenGenerationTime().getTime() - calendar.getTime().getTime() <= 0) {
            return new ResponseEntity<>("Password reset token expired", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(newPassword);
        userService.saveUser(user);

        return new ResponseEntity<>("Password reset!", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserLoginDto userLoginDto) {
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        userLoginDto.getEmail(), userLoginDto.getPassword());
        this.authenticationManager.authenticate(authenticationRequest);
        // TODO: Handle DisabledException, LockedException, BadCredentialsException

        String jwtToken = jwtService.generateToken(userLoginDto.getEmail());

        LoginResponse loginResponse = LoginResponse
                .builder().token(jwtToken).expiresIn(jwtService.getExpirationTime()).build();

        return ResponseEntity.ok(loginResponse);
    }
}
