package com.choco_tur.choco_tur.web;

import com.choco_tur.choco_tur.data.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
public class OnUserRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private User user;
    private String registrationConfirmationNumber;

    public OnUserRegistrationCompleteEvent(
            User user, Locale locale, String appUrl, String number) {
        super(user);

        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
        this.registrationConfirmationNumber = number;
    }
}
