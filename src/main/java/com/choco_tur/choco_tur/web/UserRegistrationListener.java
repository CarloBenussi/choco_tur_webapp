package com.choco_tur.choco_tur.web;

import com.choco_tur.choco_tur.data.User;
import com.choco_tur.choco_tur.service.UserService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationListener implements ApplicationListener<OnUserRegistrationCompleteEvent> {
    private final UserService userService;

    public UserRegistrationListener(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(OnUserRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnUserRegistrationCompleteEvent event) {
        User user = event.getUser();
        String number = event.getRegistrationConfirmationNumber();
        userService.saveEmailVerificationNumber(user, number);

        userService.sendEmailVerificationNumber(user.getEmail(), number, event.getLocale());
    }
}
