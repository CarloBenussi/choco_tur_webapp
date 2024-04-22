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
    static final String USER_QUIZ_SUBCOLLECTION_NAME = "quiz";
    static final String USER_TASTINGS_SUBCOLLECTION_NAME = "tastings";

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

    public List<UserQuizInfo> getUserQuizs(String email) throws ExecutionException, InterruptedException {
        Map<String, Map<String, Object>> userQuizsData =
                findAllDocumentsInSubCollection(email, USER_QUIZ_SUBCOLLECTION_NAME);
        List<UserQuizInfo> userQuizs = new ArrayList<>();
        for (String key : userQuizsData.keySet()) {
            userQuizs.add(objectMapper.convertValue(userQuizsData.get(key), UserQuizInfo.class));
        }
        return userQuizs;
    }

    public UserQuizInfo getUserQuizInfo(String email, String quizId) throws ExecutionException, InterruptedException {
        Map<String, Map<String, Object>> userQuizsData =
                findAllDocumentsInSubCollection(email, USER_QUIZ_SUBCOLLECTION_NAME);
        for (String key : userQuizsData.keySet()) {
            if (quizId.equals(userQuizsData.get(key).get("id"))) {
                return objectMapper.convertValue(userQuizsData.get(key), UserQuizInfo.class);
            }
        }

        return null;
    }

    public List<UserTastingInfo> getUserTastings(String email) throws ExecutionException, InterruptedException {
        Map<String, Map<String, Object>> userTastingsData =
                findAllDocumentsInSubCollection(email, USER_TASTINGS_SUBCOLLECTION_NAME);
        List<UserTastingInfo> userTastings = new ArrayList<>();
        for (String key : userTastingsData.keySet()) {
            userTastings.add(objectMapper.convertValue(userTastingsData.get(key), UserTastingInfo.class));
        }
        return userTastings;
    }

    public UserTastingInfo getUserTasting(String email, String tastingId) throws ExecutionException, InterruptedException {
        Map<String, Map<String, Object>> userTastingsData =
                findAllDocumentsInSubCollection(email, USER_TASTINGS_SUBCOLLECTION_NAME);
        for (String key : userTastingsData.keySet()) {
            if (tastingId.equals(userTastingsData.get(key).get("id"))) {
                return objectMapper.convertValue(userTastingsData.get(key), UserTastingInfo.class);
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

        boolean updated = false;
        for (String key : userToursData.keySet()) {
            if (userTourInfo.getId().equals(userToursData.get(key).get("id"))) {
                saveInSubCollection(user.getEmail(), USER_TOURS_SUBCOLLECTION_NAME, key,
                        objectMapper.convertValue(userTourInfo, Map.class));
                updated = true;
            }
        }

        if (!updated) {
            addInSubCollection(user.getEmail(), USER_TOURS_SUBCOLLECTION_NAME,
                    objectMapper.convertValue(userTourInfo, Map.class));
        }
    }

    public void saveUserQuiz(User user, UserQuizInfo userQuizInfo) throws ExecutionException, InterruptedException {
        Map<String, Map<String, Object>> userQuizData =
                findAllDocumentsInSubCollection(user.getEmail(), USER_QUIZ_SUBCOLLECTION_NAME);

        boolean updated = false;
        for (String key : userQuizData.keySet()) {
            if (userQuizInfo.getId().equals(userQuizData.get(key).get("id"))) {
                saveInSubCollection(user.getEmail(), USER_QUIZ_SUBCOLLECTION_NAME, key,
                        objectMapper.convertValue(userQuizInfo, Map.class));
                updated = true;
            }
        }

        if (!updated) {
            addInSubCollection(user.getEmail(), USER_QUIZ_SUBCOLLECTION_NAME,
                    objectMapper.convertValue(userQuizInfo, Map.class));
        }
    }

    public void saveUserTasting(User user, UserTastingInfo userTastingInfo) throws ExecutionException, InterruptedException {
        Map<String, Map<String, Object>> userTastingsData =
                findAllDocumentsInSubCollection(user.getEmail(), USER_TASTINGS_SUBCOLLECTION_NAME);

        boolean updated = false;
        for (String key : userTastingsData.keySet()) {
            if (userTastingInfo.getId().equals(userTastingsData.get(key).get("id"))) {
                saveInSubCollection(user.getEmail(), USER_TASTINGS_SUBCOLLECTION_NAME, key,
                        objectMapper.convertValue(userTastingInfo, Map.class));
                updated = true;
            }
        }

        if (!updated) {
            addInSubCollection(user.getEmail(), USER_TASTINGS_SUBCOLLECTION_NAME,
                    objectMapper.convertValue(userTastingInfo, Map.class));
        }
    }
}
