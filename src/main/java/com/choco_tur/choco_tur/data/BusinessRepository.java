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
public class BusinessRepository extends FirestoreRepository<Business> {

    static final String BUSINESS_COLLECTION_NAME = "businesses";

    private final ObjectMapper objectMapper;
    protected BusinessRepository(Firestore firestore, ObjectMapper objectMapper) {
        super(firestore, BUSINESS_COLLECTION_NAME);
        this.objectMapper = objectMapper;
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public Business findByEmail(String email) throws ExecutionException, InterruptedException {
        Map<String, Business> businessesMap = findAllDocuments(Business.class);
        for (String key : businessesMap.keySet()) {
            if(email.equals(businessesMap.get(key).getEmail())) {
                return businessesMap.get(key);
            }
        }

        return null;
    }

    public Business findById(String businessId) throws ExecutionException, InterruptedException {
        try {
            return findDocumentById(Business.class, businessId);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public void save(Business business) {
        save(business, business.getId());
    }
}
