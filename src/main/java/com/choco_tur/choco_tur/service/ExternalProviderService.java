package com.choco_tur.choco_tur.service;

import com.choco_tur.choco_tur.web.dto.UserExtProviderSignInDto;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.nimbusds.oauth2.sdk.GeneralException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
public class ExternalProviderService {

    private GoogleIdTokenVerifier googleIdTokenVerifier;

    public ExternalProviderService(GoogleIdTokenVerifier googleIdTokenVerifier) {
        this.googleIdTokenVerifier = googleIdTokenVerifier;
    }

    public void validateExtProviderToken(UserExtProviderSignInDto userDto)
            throws GeneralException, GeneralSecurityException, IOException {
        if (userDto.getProviderId() == 1) {
            GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(userDto.getToken());
            if (googleIdToken != null) {
                GoogleIdToken.Payload payload = googleIdToken.getPayload();
                if(!userDto.getEmail().equals(payload.getEmail())) {
                    throw new GeneralException("Email " + userDto.getEmail() +
                            " does not match Google token email " + payload.getEmail());
                }
            }
        }
        // TODO: Check tokens for Facebook login and Apple login.
        else {
            throw new GeneralException("Unsupported provider with ID " + userDto.getProviderId());
        }
    }
}
