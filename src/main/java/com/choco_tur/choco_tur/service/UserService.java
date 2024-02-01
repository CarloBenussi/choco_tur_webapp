package com.choco_tur.choco_tur.service;

import com.choco_tur.choco_tur.data.UserLoginInfo;
import com.choco_tur.choco_tur.data.UserRepository;
import com.choco_tur.choco_tur.web.UserExtProviderSignInDto;
import com.choco_tur.choco_tur.web.UserRegistrationDto;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;

@Service
public class UserService {
    private static final int EMAIL_TOKEN_EXPIRATION = 60 * 24;
    private static final int PASSWORD_RESET_TOKEN_EXPIRATION = 10;

    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private JavaMailSender mailSender;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserLoginInfo registerNewUser(UserRegistrationDto userDto) throws UserAlreadyExistAuthenticationException {
        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new UserAlreadyExistAuthenticationException("There is an account with that email address: "
                    + userDto.getEmail());
        }

        UserLoginInfo user = new UserLoginInfo();
        user.setEmail(userDto.getEmail());
        user.setPassword(encoder.encode(userDto.getPassword()));

        return userRepository.save(user);
    }

    public void signInUserWithExtProvider(UserExtProviderSignInDto userDto) throws IOException {
        // Check if a user already exists registered with the same ext provider.
        UserLoginInfo existingUser = userRepository.findByEmail(userDto.getEmail());
        if (existingUser != null) {
            if (existingUser.getExternalProviderId() == userDto.getProviderId()) {
                // Nothing to save, user is already present.
                return;
            } else {
                // TODO: Warn user that its email is already registered with either other ext
                // provider or directly.
            }
        }

        UserLoginInfo user = new UserLoginInfo();
        user.setEmail(userDto.getEmail());
        user.setEmailValidationStatus(true);
        user.setExternalProviderId(userDto.getProviderId());
        userRepository.save(user);
    }

    public void saveEmailVerificationNumber(UserLoginInfo user, String sequence) {
        // Calculate expiry date for token.
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, EMAIL_TOKEN_EXPIRATION);

        user.setEmailVerificationNumberExpirationTime(new Timestamp(cal.getTime().getTime()));
        user.setEmailVerificationNumber(sequence);
        userRepository.save(user);
    }

    public void sendEmailVerificationNumber(UserLoginInfo user, String number, String appUrl, Locale locale) {
        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String message = messageSource.getMessage("message.registrationConfirmationEmail", null, locale);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\r\n" + number);
        mailSender.send(email);
    }

    public void savePasswordResetToken(UserLoginInfo user, String token) {
        // Calculate expiry date for token.
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, PASSWORD_RESET_TOKEN_EXPIRATION);

        user.setPasswordResetTokenGenerationTime(new Timestamp(cal.getTime().getTime()));
        user.setPasswordResetToken(token);
        userRepository.save(user);
    }

    public void sendPasswordResetToken(UserLoginInfo user, String token, String appUrl, Locale locale) {
        String recipientAddress = user.getEmail();
        String subject = "Password Reset";
        String confirmationUrl
                = appUrl + "/users/changePassword?token=" + token;
        String message = messageSource.getMessage("message.passwordReset", null, locale);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\r\n" + "http://localhost:8080" + confirmationUrl);
        mailSender.send(email);
    }

    public void changeUserPassword(UserLoginInfo user, String newPassword) {
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
    }

    public UserLoginInfo getUserByEmailVerificationNumber(String number) {
        return userRepository.findByEmailVerificationNumber(number);
    }

    public UserLoginInfo getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserLoginInfo getUserByPasswordResetToken(String token) {
        return userRepository.findByPasswordResetToken(token);
    }

    public void saveUser(UserLoginInfo user) {
        userRepository.save(user);
    }
}
