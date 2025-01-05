package com.choco_tur.choco_tur.service;

import com.choco_tur.choco_tur.data.*;
import com.choco_tur.choco_tur.web.dto.BusinessRegistrationDto;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

@Service
public class BusinessService {
    private static final int EMAIL_TOKEN_EXPIRATION = 60 * 24;
    private static final int PASSWORD_RESET_TOKEN_EXPIRATION = 10;

    private final BusinessRepository businessRepository;
    private final PasswordEncoder encoder;
    private final MessageSource messageSource;
    private final JavaMailSender mailSender;

    public BusinessService(BusinessRepository businessRepository, PasswordEncoder encoder, MessageSource messageSource, JavaMailSender mailSender) {
        this.businessRepository = businessRepository;
        this.encoder = encoder;
        this.messageSource = messageSource;
        this.mailSender = mailSender;
    }

    public Business registerNewBusiness(BusinessRegistrationDto businessDto) throws UserAlreadyExistAuthenticationException, ExecutionException, InterruptedException {
        if (businessRepository.findByEmail(businessDto.getEmail()) != null) {
            throw new UserAlreadyExistAuthenticationException("There is a business account with that email address: "
                    + businessDto.getEmail());
        }

        Business business = new Business();
        business.setDeviceRegistrationToken(businessDto.getDeviceRegistrationToken());
        business.setId(businessDto.getBusinessId());
        business.setEmail(businessDto.getEmail());
        business.setPassword(encoder.encode(businessDto.getPassword()));

        saveBusiness(business);
        return business;
    }

    public void saveEmailVerificationNumber(Business business, String sequence) {
        // Calculate expiry date for token.
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, EMAIL_TOKEN_EXPIRATION);

        business.setEmailVerificationNumberExpirationTime(cal.getTime().getTime());
        business.setEmailVerificationNumber(sequence);
        saveBusiness(business);
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

    public void savePasswordResetNumber(Business business, String sequence) {
        // Calculate expiry date for token.
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, PASSWORD_RESET_TOKEN_EXPIRATION);

        business.setPasswordResetNumberGenerationTime(cal.getTime().getTime());
        business.setPasswordResetNumber(sequence);
        saveBusiness(business);
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

    public void sendPasswordResetToken(Business business, String token, String appUrl, Locale locale) {
        String recipientAddress = business.getEmail();
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

    public void changeUserPassword(Business business, String newPassword) {
        business.setPassword(encoder.encode(newPassword));
        saveBusiness(business);
    }

    public String encodePassword(String password) {
        return encoder.encode(password);
    }

    public Business getBusinessByEmail(String email) throws ExecutionException, InterruptedException {
        return businessRepository.findByEmail(email);
    }

    public Business getBusinessById(String businessId) throws ExecutionException, InterruptedException {
        return businessRepository.findById(businessId);
    }

    public void saveBusiness(Business business) {
        businessRepository.save(business);
    }
}
