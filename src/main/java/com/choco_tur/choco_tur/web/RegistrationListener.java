package com.choco_tur.choco_tur.web;

import com.choco_tur.choco_tur.data.UserLoginInfo;
import com.choco_tur.choco_tur.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    private final UserService userService;

    public RegistrationListener(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        UserLoginInfo user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveEmailVerificationToken(user, token);

        userService.sendEmailVerificationToken(user, token, event.getAppUrl(), event.getLocale());
    }
}
