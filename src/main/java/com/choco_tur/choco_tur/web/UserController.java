package com.choco_tur.choco_tur.web;

import com.choco_tur.choco_tur.data.User;
import com.choco_tur.choco_tur.data.UserTourInfo;
import com.choco_tur.choco_tur.service.ExternalProviderService;
import com.choco_tur.choco_tur.service.JwtService;
import com.choco_tur.choco_tur.service.UserAlreadyExistAuthenticationException;
import com.choco_tur.choco_tur.utils.CommonUtils;
import com.choco_tur.choco_tur.web.dto.UserExtProviderSignInDto;
import com.choco_tur.choco_tur.web.dto.UserLoginDto;
import com.choco_tur.choco_tur.web.dto.UserLoginWithTokenDto;
import com.choco_tur.choco_tur.web.dto.UserRegistrationDto;
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
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

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
    ) throws ExecutionException, InterruptedException, UserAlreadyExistAuthenticationException {
        User registered = userService.registerNewUser(userDto);

        String appUrl = request.getContextPath();
        applicationEventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered,
                request.getLocale(), appUrl));

        String responseMessage = messageSource.getMessage("registrationSuccess",
                null, request.getLocale());
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PostMapping("/signInWithExtProvider")
    public ResponseEntity<Object> signInWithExtProvider(
        @Valid @RequestBody UserExtProviderSignInDto userDto,
        HttpServletRequest request
    ) throws GeneralSecurityException, ExecutionException, InterruptedException {
        try {
            externalProviderService.validateExtProviderToken(userDto);

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

            String jwtAccessToken = jwtService.generateAccessToken(userDto.getEmail());
            String jwtRefreshToken = jwtService.generateRefreshToken(userDto.getEmail());

            LoginResponse loginResponse = LoginResponse.builder()
                    .accessToken(jwtAccessToken)
                    .accessTokenExpiresIn(jwtService.getAccessTokenExpirationTime())
                    .refreshToken(jwtRefreshToken)
                    .refreshTokenExpiresIn(jwtService.getRefreshTokenExpirationTime())
                    .build();

            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException | IOException | GeneralException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/registrationConfirmation")
    public ResponseEntity<?> confirmRegistration(
            @RequestParam("email")String email,
            @RequestParam("number")String number
    ) throws ExecutionException, InterruptedException {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return new ResponseEntity<>("No user found by email", HttpStatus.BAD_REQUEST);
        }

        if (!number.equals(user.getEmailVerificationNumber())) {
            return new ResponseEntity<>("Authentication number is wrong", HttpStatus.BAD_REQUEST);
        }

        Calendar calendar = Calendar.getInstance();
        if (user.getEmailVerificationNumberExpirationTime() - calendar.getTime().getTime() <= 0) {
            return new ResponseEntity<>("Authentication number expired", HttpStatus.BAD_REQUEST);
        }

        user.setEmailValidationStatus(true);
        user.setEmailVerificationNumber(null);
        user.setEmailVerificationNumberExpirationTime(-1);
        userService.saveUser(user);

        String jwtAccessToken = jwtService.generateAccessToken(email);
        String jwtRefreshToken = jwtService.generateRefreshToken(email);
        LoginResponse loginResponse = LoginResponse.builder()
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
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return new ResponseEntity<>("No user found with email " + email, HttpStatus.BAD_REQUEST);
        }
        if (!user.getEmailVerificationNumber().equals(number)) {
            return new ResponseEntity<>("User with email " + email + " has different verification number", HttpStatus.BAD_REQUEST);
        }

        String newNumber = CommonUtils.getSixDigitNumberSequence();
        userService.saveEmailVerificationNumber(user, newNumber);
        userService.sendEmailVerificationNumber(user, newNumber, request.getLocale());

        return new ResponseEntity<>("Verification email resent", HttpStatus.OK);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(
        @RequestParam("email")String email,
        HttpServletRequest request
    ) throws ExecutionException, InterruptedException {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return new ResponseEntity<>("No user found with email " + email, HttpStatus.BAD_REQUEST);
        }

        String token = UUID.randomUUID().toString();
        userService.savePasswordResetToken(user, token);

        userService.sendPasswordResetToken(user, token, request.getContextPath(), request.getLocale());

        return new ResponseEntity<>("Password reset email resent", HttpStatus.OK);
    }

    @GetMapping("/changePassword")
    public ResponseEntity<String> changePassword(
        @RequestParam("email")String email,
        @RequestParam("token")String token
    ) throws ExecutionException, InterruptedException {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return new ResponseEntity<>("No user found with email " + email, HttpStatus.BAD_REQUEST);
        }
        if (!user.getPasswordResetToken().equals(token)) {
            return new ResponseEntity<>("User with email " + email + " has different password reset token", HttpStatus.BAD_REQUEST);
        }

        Calendar calendar = Calendar.getInstance();
        if (user.getPasswordResetTokenGenerationTime() - calendar.getTime().getTime() <= 0) {
            return new ResponseEntity<>("Password reset token expired", HttpStatus.BAD_REQUEST);
        }

        // TODO: Redirect to html page for password reset.
        return new ResponseEntity<>("Reset password please", HttpStatus.OK);
    }

    @PostMapping("/savePassword")
    public ResponseEntity<String> savePassword(
        @RequestParam("email")String email,
        @RequestParam("token")String token,
        @RequestParam("password")String newPassword
    ) throws ExecutionException, InterruptedException {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return new ResponseEntity<>("No user found with email " + email, HttpStatus.BAD_REQUEST);
        }
        if (!user.getPasswordResetToken().equals(token)) {
            return new ResponseEntity<>("User with email " + email + " has different password reset token", HttpStatus.BAD_REQUEST);
        }

        Calendar calendar = Calendar.getInstance();
        if (user.getPasswordResetTokenGenerationTime() - calendar.getTime().getTime() <= 0) {
            return new ResponseEntity<>("Password reset token expired", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(newPassword);
        userService.saveUser(user);

        return new ResponseEntity<>("Password reset!", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserLoginDto userLoginDto) throws ExecutionException, InterruptedException {
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        userLoginDto.getEmail(), userLoginDto.getPassword());
        this.authenticationManager.authenticate(authenticationRequest);
        // TODO: Handle DisabledException, LockedException

        User user = userService.getUserByEmail(userLoginDto.getEmail());

        String jwtAccessToken = jwtService.generateAccessToken(userLoginDto.getEmail());
        String jwtRefreshToken = jwtService.generateRefreshToken(userLoginDto.getEmail());
        LoginResponse loginResponse = LoginResponse.builder()
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
            return new ResponseEntity<>("Token expired.", HttpStatus.UNAUTHORIZED);
        }

        User user = userService.getUserByEmail(userLoginWithTokenDto.getEmail());

        return ResponseEntity.ok("Login with token successful!");
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
        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(jwtAccessToken)
                .accessTokenExpiresIn(jwtService.getAccessTokenExpirationTime())
                .build();

        return ResponseEntity.ok(loginResponse);
    }
}
