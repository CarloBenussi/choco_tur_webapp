package com.choco_tur.choco_tur.web;

import com.choco_tur.choco_tur.data.Business;
import com.choco_tur.choco_tur.service.BusinessService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class BusinessRegistrationListener implements ApplicationListener<OnBusinessRegistrationCompleteEvent> {
    private final BusinessService businessService;

    public BusinessRegistrationListener(BusinessService businessService) {
        this.businessService = businessService;
    }

    @Override
    public void onApplicationEvent(OnBusinessRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnBusinessRegistrationCompleteEvent event) {
        Business business = event.getBusiness();
        String number = event.getRegistrationConfirmationNumber();
        businessService.saveEmailVerificationNumber(business, number);

        businessService.sendEmailVerificationNumber(business.getEmail(), number, event.getLocale());
    }
}
