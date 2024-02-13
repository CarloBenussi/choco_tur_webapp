package com.choco_tur.choco_tur.data;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    public void save(User user) {
        save(user, user.getEmail());
    }
}
