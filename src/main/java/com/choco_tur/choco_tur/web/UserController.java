package com.choco_tur.choco_tur.web;

import com.choco_tur.choco_tur.data.UserLoginInfo;
import com.choco_tur.choco_tur.service.JwtService;
import com.choco_tur.choco_tur.service.UserAlreadyExistAuthenticationException;
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

import java.util.Calendar;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final MessageSource messageSource;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public UserController(
            UserService userService,
            ApplicationEventPublisher applicationEventPublisher,
            MessageSource messageSource,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userService = userService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.messageSource = messageSource;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/registration")
    public ResponseEntity<String> registerUsername(
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

    @GetMapping("/registrationConfirmation")
    public ResponseEntity<String> confirmRegistration(
            @RequestParam("token")String token
    ) {
        UserLoginInfo user = userService.getUserByEmailVerificationToken(token);
        if (user == null) {
            return new ResponseEntity<>("No user found by email verification token", HttpStatus.BAD_REQUEST);
        }

        Calendar calendar = Calendar.getInstance();
        if (user.getEmailVerificationTokenExpirationTime().getTime() - calendar.getTime().getTime() <= 0) {
            return new ResponseEntity<>("Authentication token expired", HttpStatus.BAD_REQUEST);
        }

        user.setEmailValidationStatus(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpirationTime(null);
        userService.saveUser(user);

        return new ResponseEntity<>("Email confirmed!", HttpStatus.OK);
    }

    @GetMapping("/resendEmailVerificationToken")
    public ResponseEntity<String> resendEmailVerificationToken(
            @RequestParam("token")String token,
            HttpServletRequest request
    ) {
        UserLoginInfo user = userService.getUserByEmailVerificationToken(token);
        if (user == null) {
            return new ResponseEntity<>("No user found by email verification token", HttpStatus.BAD_REQUEST);
        }

        String newToken = UUID.randomUUID().toString();
        userService.saveEmailVerificationToken(user, newToken);

        userService.sendEmailVerificationToken(user, newToken, request.getContextPath(), request.getLocale());

        return new ResponseEntity<>("Verification email resent", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserLoginDto userLoginDto) {
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        userLoginDto.getEmail(), userLoginDto.getPassword());
        this.authenticationManager.authenticate(authenticationRequest);

        String jwtToken = jwtService.generateToken(userLoginDto.getEmail());

        LoginResponse loginResponse = LoginResponse
                .builder().token(jwtToken).expiresIn(jwtService.getExpirationTime()).build();

        return ResponseEntity.ok(loginResponse);
    }

}
