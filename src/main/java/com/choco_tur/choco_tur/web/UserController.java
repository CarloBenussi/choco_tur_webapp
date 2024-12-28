package com.choco_tur.choco_tur.web;

import com.choco_tur.choco_tur.data.User;
import com.choco_tur.choco_tur.data.UserAnswerInfo;
import com.choco_tur.choco_tur.service.ExternalProviderService;
import com.choco_tur.choco_tur.service.JwtService;
import com.choco_tur.choco_tur.service.UserAlreadyExistAuthenticationException;
import com.choco_tur.choco_tur.utils.CommonUtils;
import com.choco_tur.choco_tur.web.dto.*;
import com.nimbusds.oauth2.sdk.GeneralException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        String number = CommonUtils.getSixDigitNumberSequence();
        applicationEventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered,
                request.getLocale(), appUrl, number));

        return new ResponseEntity<>(number, HttpStatus.OK);
    }

    @PostMapping("/signInWithExtProvider")
    public ResponseEntity<Object> signInWithExtProvider(
        @Valid @RequestBody UserExtProviderSignInDto userDto,
        HttpServletRequest request
    ) throws GeneralSecurityException, ExecutionException, InterruptedException {
        try {
            externalProviderService.validateExtProviderToken(userDto);

            // Here the 'authenticate' call would check that the hash of the passed password
            // matches the hash of the password saved for this user. We can simply skip this step and generate the jwt token
            // directly
            // Authentication authenticationRequest =
            //         UsernamePasswordAuthenticationToken.unauthenticated(
            //                 userDto.getEmail(), null);
            // this.authenticationManager.authenticate(authenticationRequest);

            User user = User.builder()
                .email(userDto.getEmail())
                .emailValidationStatus(true)
                .externalProviderId(1)
                .build();
            userService.saveUser(user);

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

        String appUrl = request.getContextPath();
        String newNumber = CommonUtils.getSixDigitNumberSequence();
        applicationEventPublisher.publishEvent(new OnRegistrationCompleteEvent(user,
                request.getLocale(), appUrl, newNumber));

        return new ResponseEntity<>(number, HttpStatus.OK);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(
        @RequestBody String email,
        HttpServletRequest request
    ) throws ExecutionException, InterruptedException {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return new ResponseEntity<>("No user found with email " + email, HttpStatus.BAD_REQUEST);
        }

        String number = CommonUtils.getSixDigitNumberSequence();
        userService.savePasswordResetNumber(user, number);
        userService.sendPasswordResetNumber(user.getEmail(), number, request.getLocale());

        return new ResponseEntity<>("Password reset number sent", HttpStatus.OK);
    }

    @GetMapping("/resetPasswordTest")
    public ResponseEntity<String> changePassword(
        @RequestParam("email")String email,
        @RequestParam("number")String number
    ) throws ExecutionException, InterruptedException {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return new ResponseEntity<>("No user found with email " + email, HttpStatus.BAD_REQUEST);
        }
        if (!user.getPasswordResetNumber().equals(number)) {
            return new ResponseEntity<>("User with email " + email + " has different verification number", HttpStatus.BAD_REQUEST);
        }
        Calendar calendar = Calendar.getInstance();
        if (user.getPasswordResetNumberGenerationTime() - calendar.getTime().getTime() <= 0) {
            return new ResponseEntity<>("Password reset number expired", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("OK, proceed", HttpStatus.OK);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> savePassword(
            @Valid @RequestBody ChangePasswordDto changePasswordDto
    ) throws ExecutionException, InterruptedException {
        User user = userService.getUserByEmail(changePasswordDto.getEmail());
        if (user == null) {
            return new ResponseEntity<>("No user found with email " + changePasswordDto.getEmail(), HttpStatus.BAD_REQUEST);
        }
        if (!user.getPasswordResetNumber().equals(changePasswordDto.getPasswordRecoveryNumber())) {
            return new ResponseEntity<>("User with email " + changePasswordDto.getEmail() + " has different password reset number", HttpStatus.BAD_REQUEST);
        }

        Calendar calendar = Calendar.getInstance();
        if (user.getPasswordResetNumberGenerationTime() - calendar.getTime().getTime() <= 0) {
            return new ResponseEntity<>("Password reset number expired", HttpStatus.BAD_REQUEST);
        }

        String encodedPassword = userService.encodePassword(changePasswordDto.getPassword());
        user.setPassword(encodedPassword);
        user.setPasswordResetNumber(null);
        user.setPasswordResetNumberGenerationTime(-1);
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
            return new ResponseEntity<>("Token expired", HttpStatus.UNAUTHORIZED);
        }

        User user = userService.getUserByEmail(userLoginWithTokenDto.getEmail());

        String jwtRefreshToken = jwtService.generateRefreshToken(userLoginWithTokenDto.getEmail());
        LoginResponse loginResponse = LoginResponse.builder()
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
        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(jwtAccessToken)
                .accessTokenExpiresIn(jwtService.getAccessTokenExpirationTime())
                .refreshToken(jwtRefreshToken)
                .refreshTokenExpiresIn(jwtService.getRefreshTokenExpirationTime())
                .build();

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/info/getCoins")
    public ResponseEntity<?> getCoins() throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userService.getUserByEmail(userDetails.getUsername());

        return ResponseEntity.ok(user.getCollectedCoins());
    }

    @PostMapping("/info/addCoins")
    public ResponseEntity<?> addCoins(@RequestBody int coins) throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userService.getUserByEmail(userDetails.getUsername());
        user.setCollectedCoins(user.getCollectedCoins() + coins);
        userService.saveUser(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/info/removeCoins")
    public ResponseEntity<?> removeCoins(@RequestBody int coins) throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userService.getUserByEmail(userDetails.getUsername());
        user.setCollectedCoins(user.getCollectedCoins() - coins);
        userService.saveUser(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/info/getAnswers")
    public ResponseEntity<?> getUserAnswers() throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userService.getUserByEmail(userDetails.getUsername());
        List<UserAnswerInfo> userAnswerInfos = userService.getUserAnswerInfos(user);

        return ResponseEntity.ok(userAnswerInfos);
    }

    @PostMapping("/info/recordAnswer")
    public ResponseEntity<?> recordAnswer(@RequestBody String answerId) throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        UserAnswerInfo userAnswerInfo = new UserAnswerInfo();
        userAnswerInfo.setId(answerId);
        User user = userService.getUserByEmail(userDetails.getUsername());
        userService.saveUserAnswer(user, userAnswerInfo);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
