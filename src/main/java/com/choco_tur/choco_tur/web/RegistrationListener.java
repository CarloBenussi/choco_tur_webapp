package com.choco_tur.choco_tur.web;

import com.choco_tur.choco_tur.data.UserLoginInfo;
import com.choco_tur.choco_tur.service.UserService;
import com.choco_tur.choco_tur.utils.CommonUtils;
import org.springframework.context.ApplicationListener;
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
        String number = CommonUtils.getSixDigitNumberSequence();
        userService.saveEmailVerificationNumber(user, number);

        userService.sendEmailVerificationNumber(user, number, event.getAppUrl(), event.getLocale());
    }
}
