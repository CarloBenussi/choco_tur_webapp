package com.choco_tur.choco_tur.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "com.chocotur")
@Getter
@Setter
public class ConfigProperties {
    private String senderMail;
    private String senderPassword;
    private String googleClientId;
    private String firestoreServiceAccount;
}
