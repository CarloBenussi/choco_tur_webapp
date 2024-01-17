package com.choco_tur.choco_tur.web;

import com.choco_tur.choco_tur.data.UserLoginInfo;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private UserLoginInfo user;

    public OnRegistrationCompleteEvent(
            UserLoginInfo user, Locale locale, String appUrl) {
        super(user);

        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }
}
