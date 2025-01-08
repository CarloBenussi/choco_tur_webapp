package com.choco_tur.choco_tur.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

@Repository
public class UserRepository extends FirestoreRepository<User> {

    static final String USERS_COLLECTION_NAME = "users";
    static final String USER_TOURS_SUBCOLLECTION_NAME = "tours";
    static final String USER_QUIZS_SUBCOLLECTION_NAME = "quizs";
    static final String USER_TASTINGS_SUBCOLLECTION_NAME = "tastings";
    static final String USER_ANSWERS_SUBCOLLECTION_NAME = "answers";
    static final String USER_PURCHASES_SUBCOLLECTION_NAME = "purchases";

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
            if (tourId.equals(key)) {
                return objectMapper.convertValue(userToursData.get(key), UserTourInfo.class);
            }
        }

        return null;
    }

    public List<UserQuizInfo> getUserQuizs(String email) throws ExecutionException, InterruptedException {
        Map<String, Map<String, Object>> userQuizsData =
                findAllDocumentsInSubCollection(email, USER_QUIZS_SUBCOLLECTION_NAME);
        List<UserQuizInfo> userQuizs = new ArrayList<>();
        for (String key : userQuizsData.keySet()) {
            userQuizs.add(objectMapper.convertValue(userQuizsData.get(key), UserQuizInfo.class));
        }
        return userQuizs;
    }

    public UserQuizInfo getUserQuizInfo(String email, String quizId) throws ExecutionException, InterruptedException {
        Map<String, Map<String, Object>> userQuizsData =
                findAllDocumentsInSubCollection(email, USER_QUIZS_SUBCOLLECTION_NAME);
        for (String key : userQuizsData.keySet()) {
            if (quizId.equals(key)) {
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
            if (tastingId.equals(key)) {
                return objectMapper.convertValue(userTastingsData.get(key), UserTastingInfo.class);
            }
        }

        return null;
    }

    public List<UserAnswerInfo> getUserAnswers(String email) throws ExecutionException, InterruptedException {
        Map<String, Map<String, Object>> userAnswersData =
                findAllDocumentsInSubCollection(email, USER_ANSWERS_SUBCOLLECTION_NAME);
        List<UserAnswerInfo> userAnswers = new ArrayList<>();
        for (String key : userAnswersData.keySet()) {
            userAnswers.add(objectMapper.convertValue(userAnswersData.get(key), UserAnswerInfo.class));
        }
        return userAnswers;
    }

    public UserAnswerInfo getUserAnswer(String email, String answerId) throws ExecutionException, InterruptedException {
        Map<String, Map<String, Object>> userAnswersData =
                findAllDocumentsInSubCollection(email, USER_ANSWERS_SUBCOLLECTION_NAME);
        for (String key : userAnswersData.keySet()) {
            if (answerId.equals(key)) {
                return objectMapper.convertValue(userAnswersData.get(key), UserAnswerInfo.class);
            }
        }

        return null;
    }

    public List<UserPurchaseInfo> getUserPurchases(String email) throws ExecutionException, InterruptedException {
        Map<String, Map<String, Object>> userPurchaseData =
                findAllDocumentsInSubCollection(email, USER_PURCHASES_SUBCOLLECTION_NAME);
        List<UserPurchaseInfo> userPurchaseInfos = new ArrayList<>();
        for (String key : userPurchaseData.keySet()) {
            userPurchaseInfos.add(objectMapper.convertValue(userPurchaseData.get(key), UserPurchaseInfo.class));
        }
        return userPurchaseInfos;
    }

    public UserPurchaseInfo getUserPurchase(String email, String purchaseId) throws ExecutionException, InterruptedException {
        Map<String, Map<String, Object>> userPurchaseData =
                findAllDocumentsInSubCollection(email, USER_PURCHASES_SUBCOLLECTION_NAME);
        for (String key : userPurchaseData.keySet()) {
            if (purchaseId.equals(key)) {
                return objectMapper.convertValue(userPurchaseData.get(key), UserPurchaseInfo.class);
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
            if (userTourInfo.getId().equals(key)) {
                saveInSubCollection(user.getEmail(), USER_TOURS_SUBCOLLECTION_NAME, key,
                        objectMapper.convertValue(userTourInfo, Map.class));
                updated = true;
            }
        }

        if (!updated) {
            addInSubCollection(user.getEmail(), USER_TOURS_SUBCOLLECTION_NAME, userTourInfo.getId(),
                    objectMapper.convertValue(userTourInfo, Map.class));
        }
    }

    public void saveUserQuiz(User user, UserQuizInfo userQuizInfo) throws ExecutionException, InterruptedException {
        Map<String, Map<String, Object>> userQuizData =
                findAllDocumentsInSubCollection(user.getEmail(), USER_QUIZS_SUBCOLLECTION_NAME);

        boolean updated = false;
        for (String key : userQuizData.keySet()) {
            if (userQuizInfo.getId().equals(key)) {
                saveInSubCollection(user.getEmail(), USER_QUIZS_SUBCOLLECTION_NAME, key,
                        objectMapper.convertValue(userQuizInfo, Map.class));
                updated = true;
            }
        }

        if (!updated) {
            addInSubCollection(user.getEmail(), USER_QUIZS_SUBCOLLECTION_NAME, userQuizInfo.getId(),
                    objectMapper.convertValue(userQuizInfo, Map.class));
        }
    }

    public void saveUserTasting(User user, UserTastingInfo userTastingInfo) throws ExecutionException, InterruptedException {
        Map<String, Map<String, Object>> userTastingsData =
                findAllDocumentsInSubCollection(user.getEmail(), USER_TASTINGS_SUBCOLLECTION_NAME);

        boolean updated = false;
        for (String key : userTastingsData.keySet()) {
            if (userTastingInfo.getId().equals(key)) {
                saveInSubCollection(user.getEmail(), USER_TASTINGS_SUBCOLLECTION_NAME, key,
                        objectMapper.convertValue(userTastingInfo, Map.class));
                updated = true;
            }
        }

        if (!updated) {
            addInSubCollection(user.getEmail(), USER_TASTINGS_SUBCOLLECTION_NAME, userTastingInfo.getId(),
                    objectMapper.convertValue(userTastingInfo, Map.class));
        }
    }

    public void saveUserAnswer(User user, UserAnswerInfo userAnswerInfo) throws ExecutionException, InterruptedException {
        Map<String, Map<String, Object>> userAnswersData =
                findAllDocumentsInSubCollection(user.getEmail(), USER_ANSWERS_SUBCOLLECTION_NAME);

        boolean updated = false;
        for (String key : userAnswersData.keySet()) {
            if (userAnswerInfo.getId().equals(key)) {
                saveInSubCollection(user.getEmail(), USER_ANSWERS_SUBCOLLECTION_NAME, key,
                        objectMapper.convertValue(userAnswerInfo, Map.class));
                updated = true;
            }
        }

        if (!updated) {
            addInSubCollection(user.getEmail(), USER_ANSWERS_SUBCOLLECTION_NAME, userAnswerInfo.getId(),
                    objectMapper.convertValue(userAnswerInfo, Map.class));
        }
    }

    public void saveUserPurchase(User user, UserPurchaseInfo userPurchaseInfo) throws ExecutionException, InterruptedException {
        Map<String, Map<String, Object>> userPurchaseData =
                findAllDocumentsInSubCollection(user.getEmail(), USER_PURCHASES_SUBCOLLECTION_NAME);

        boolean updated = false;
        for (String key : userPurchaseData.keySet()) {
            if (userPurchaseInfo.getId().equals(key)) {
                saveInSubCollection(user.getEmail(), USER_PURCHASES_SUBCOLLECTION_NAME, key,
                        objectMapper.convertValue(userPurchaseInfo, Map.class));
                updated = true;
            }
        }

        if (!updated) {
            addInSubCollection(user.getEmail(), USER_PURCHASES_SUBCOLLECTION_NAME, userPurchaseInfo.getId(),
                    objectMapper.convertValue(userPurchaseInfo, Map.class));
        }
    }
}
