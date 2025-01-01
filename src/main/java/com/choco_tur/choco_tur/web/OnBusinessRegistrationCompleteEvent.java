package com.choco_tur.choco_tur.web;

import com.choco_tur.choco_tur.data.Business;
import com.choco_tur.choco_tur.data.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
public class OnBusinessRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private Business business;
    private String registrationConfirmationNumber;

    public OnBusinessRegistrationCompleteEvent(
            Business business, Locale locale, String appUrl, String number) {
        super(business);

        this.business = business;
        this.locale = locale;
        this.appUrl = appUrl;
        this.registrationConfirmationNumber = number;
    }
}
