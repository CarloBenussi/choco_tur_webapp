package com.choco_tur.choco_tur.service;

import com.choco_tur.choco_tur.data.*;
import com.choco_tur.choco_tur.web.dto.UserExtProviderSignInDto;
import com.choco_tur.choco_tur.web.dto.UserRegistrationDto;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {
    private static final int EMAIL_TOKEN_EXPIRATION = 60 * 24;
    private static final int PASSWORD_RESET_TOKEN_EXPIRATION = 10;

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final MessageSource messageSource;
    private final JavaMailSender mailSender;

    public UserService(UserRepository userRepository, PasswordEncoder encoder, MessageSource messageSource, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.messageSource = messageSource;
        this.mailSender = mailSender;
    }

    public User registerNewUser(UserRegistrationDto userDto) throws UserAlreadyExistAuthenticationException, ExecutionException, InterruptedException {
        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new UserAlreadyExistAuthenticationException("There is an account with that email address: "
                    + userDto.getEmail());
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(encoder.encode(userDto.getPassword()));
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setNationality(userDto.getNationality());
        user.setCollectedCoins(0);

        saveUser(user);
        return user;
    }

    public User signInUserWithExtProvider(UserExtProviderSignInDto userDto) throws IOException, ExecutionException, InterruptedException {
        // Check if a user already exists registered with the same ext provider.
        User existingUser = userRepository.findByEmail(userDto.getEmail());
        if (existingUser != null) {
            if (existingUser.getExternalProviderId() == userDto.getProviderId()) {
                // Nothing to save, user is already present.
                return existingUser;
            } else {
                // TODO: Warn user that its email is already registered with either other ext
                // provider or directly.
            }
        }

        // TODO: Validate token depending on ext provider.

        // TODO: Extract user data using ext provider (date of birth, nationality).

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setEmailValidationStatus(true);
        user.setExternalProviderId(userDto.getProviderId());
        saveUser(user);
        return user;
    }

    public void saveEmailVerificationNumber(User user, String sequence) {
        // Calculate expiry date for token.
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, EMAIL_TOKEN_EXPIRATION);

        user.setEmailVerificationNumberExpirationTime(cal.getTime().getTime());
        user.setEmailVerificationNumber(sequence);
        saveUser(user);
    }

    public void sendEmailVerificationNumber(String email, String number, Locale locale) {
        String subject = "Registration Confirmation";
        String message = messageSource.getMessage("registrationConfirmationEmail", null, locale);

        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(email);
        emailMessage.setSubject(subject);
        emailMessage.setText(message + "\r\n" + number);
        mailSender.send(emailMessage);
    }

    public void savePasswordResetNumber(User user, String sequence) {
        // Calculate expiry date for token.
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, PASSWORD_RESET_TOKEN_EXPIRATION);

        user.setPasswordResetNumberGenerationTime(cal.getTime().getTime());
        user.setPasswordResetNumber(sequence);
        saveUser(user);
    }

    public void sendPasswordResetNumber(String email, String number, Locale locale) {
        String subject = "Password Reset";
        String message = messageSource.getMessage("passwordResetEmail", null, locale);

        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(email);
        emailMessage.setSubject(subject);
        emailMessage.setText(message + "\r\n" + number);
        mailSender.send(emailMessage);
    }

    public void sendPasswordResetToken(User user, String token, String appUrl, Locale locale) {
        String recipientAddress = user.getEmail();
        String subject = "Password Reset";
        String confirmationUrl
                = appUrl + "/users/changePassword?token=" + token;
        String message = messageSource.getMessage("passwordReset", null, locale);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\r\n" + "http://localhost:8080" + confirmationUrl);
        mailSender.send(email);
    }

    public void changeUserPassword(User user, String newPassword) {
        user.setPassword(encoder.encode(newPassword));
        saveUser(user);
    }

    public User getUserByEmail(String email) throws ExecutionException, InterruptedException {
        return userRepository.findByEmail(email);
    }

    public List<UserTourInfo> getUserTourInfos(User user) throws ExecutionException, InterruptedException {
        return userRepository.getUserTours(user.getEmail());
    }

    public UserTourInfo getUserTourInfo(User user, String tourId) throws ExecutionException, InterruptedException {
        return userRepository.getUserTour(user.getEmail(), tourId);
    }

    public List<UserQuizInfo> getUserQuizInfos(User user) throws ExecutionException, InterruptedException {
        return userRepository.getUserQuizs(user.getEmail());
    }

    public UserQuizInfo getUserQuizInfo(User user, String quizId) throws ExecutionException, InterruptedException {
        return userRepository.getUserQuizInfo(user.getEmail(), quizId);
    }

    public List<UserTastingInfo> getUserTastingInfos(User user) throws ExecutionException, InterruptedException {
        return userRepository.getUserTastings(user.getEmail());
    }

    public UserTastingInfo getUserTastingInfo(User user, String tastingId) throws ExecutionException, InterruptedException {
        return userRepository.getUserTasting(user.getEmail(), tastingId);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void saveUserTour(User user, UserTourInfo userTourInfo) throws ExecutionException, InterruptedException {
        userRepository.saveUserTour(user, userTourInfo);
    }

    public void saveUserQuiz(User user, UserQuizInfo userQuizInfo) throws ExecutionException, InterruptedException {
        userRepository.saveUserQuiz(user, userQuizInfo);
    }

    public void saveUserTasting(User user, UserTastingInfo userTastingInfo) throws ExecutionException, InterruptedException {
        userRepository.saveUserTasting(user, userTastingInfo);
    }
}
