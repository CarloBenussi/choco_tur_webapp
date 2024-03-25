package com.choco_tur.choco_tur.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.cloud.firestore.Firestore;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class UserRepository extends FirestoreRepository<User> {

    static final String USERS_COLLECTION_NAME = "users";
    static final String USER_TOURS_SUBCOLLECTION_NAME = "tours";

    private final ObjectMapper objectMapper;
    protected UserRepository(Firestore firestore, ObjectMapper objectMapper) {
        super(firestore, USERS_COLLECTION_NAME);
        this.objectMapper = objectMapper;
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public User findByEmail(String email) throws ExecutionException, InterruptedException {
        try {
            return findDocumentById(User.class, email);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public List<UserTourInfo> getUserTours(String email) throws ExecutionException, InterruptedException {
        Map<String, Map<String, Object>> userToursData =
                findAllDocumentsInSubCollection(email, USER_TOURS_SUBCOLLECTION_NAME);
        List<UserTourInfo> userTours = new ArrayList<>();
        for (String key : userToursData.keySet()) {
            userTours.add(objectMapper.convertValue(userToursData.get(key), UserTourInfo.class));
        }
        return userTours;
    }

    public UserTourInfo getUserTour(String email, String tourId) throws ExecutionException, InterruptedException {
        Map<String, Map<String, Object>> userToursData =
                findAllDocumentsInSubCollection(email, USER_TOURS_SUBCOLLECTION_NAME);
        for (String key : userToursData.keySet()) {
            if (tourId.equals(userToursData.get(key).get("id"))) {
                return objectMapper.convertValue(userToursData.get(key), UserTourInfo.class);
            }
        }

        return null;
    }

    public void save(User user) {
        save(user, user.getEmail());
    }

    public void saveUserTour(User user, UserTourInfo userTourInfo) throws ExecutionException, InterruptedException {
        Map<String, Map<String, Object>> userToursData =
                findAllDocumentsInSubCollection(user.getEmail(), USER_TOURS_SUBCOLLECTION_NAME);
        for (String key : userToursData.keySet()) {
            if (userTourInfo.getId().equals(userToursData.get(key).get("id"))) {
                saveInSubCollection(user.getEmail(), USER_TOURS_SUBCOLLECTION_NAME, key,
                        objectMapper.convertValue(userTourInfo, Map.class));
            }
        }
    }
}
