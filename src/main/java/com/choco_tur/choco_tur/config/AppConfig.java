package com.choco_tur.choco_tur.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.FirebaseDatabase;
import org.apache.tomcat.jni.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class AppConfig {

    @Autowired
    private ConfigProperties configProperties;

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public Firestore firestore() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(configProperties.getFirestoreServiceAccount()).getFile());
        FileInputStream fileInputStream = new FileInputStream(file);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(fileInputStream))
                .build();
        FirebaseApp.initializeApp(options);

        return FirestoreClient.getFirestore();
    }
}
