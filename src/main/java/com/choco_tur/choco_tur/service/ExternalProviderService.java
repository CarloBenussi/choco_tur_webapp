package com.choco_tur.choco_tur.service;

import com.choco_tur.choco_tur.config.ConfigProperties;
import com.choco_tur.choco_tur.web.dto.UserExtProviderSignInDto;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.nimbusds.oauth2.sdk.GeneralException;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class ExternalProviderService {

    private GoogleIdTokenVerifier googleIdTokenVerifier;

    private ConfigProperties configProperties;

    public ExternalProviderService(GoogleIdTokenVerifier googleIdTokenVerifier, ConfigProperties configProperties) {
        this.configProperties = configProperties;
        this.googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
            .setAudience(Collections.singletonList(configProperties.getGoogleClientId()))
            .build();
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
