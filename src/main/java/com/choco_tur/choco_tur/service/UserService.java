package com.choco_tur.choco_tur.service;

import com.choco_tur.choco_tur.data.UserLoginInfo;
import com.choco_tur.choco_tur.data.UserRepository;
import com.choco_tur.choco_tur.web.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;

@Service
public class UserService {
    private static final int EXPIRATION = 60 * 24;

    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private MessageSource messages;

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

    public void saveEmailVerificationToken(UserLoginInfo user, String token) {
        // Calculate expiry date for token.
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, EXPIRATION);

        user.setEmailVerificationTokenExpirationTime(new Timestamp(cal.getTime().getTime()));
        user.setEmailVerificationToken(token);
        userRepository.save(user);
    }

    public void sendEmailVerificationToken(UserLoginInfo user, String token, String appUrl, Locale locale) {
        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl
                = appUrl + "/users/registrationConfirmation?token=" + token;
        String message = messages.getMessage("message.registrationConfirmationEmail", null, locale);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\r\n" + "http://localhost:8080" + confirmationUrl);
        mailSender.send(email);
    }

    public UserLoginInfo getUserByEmailVerificationToken(String token) {
        return userRepository.findByEmailVerificationToken(token);
    }

    public void saveUser(UserLoginInfo user) {
        userRepository.save(user);
    }
}
