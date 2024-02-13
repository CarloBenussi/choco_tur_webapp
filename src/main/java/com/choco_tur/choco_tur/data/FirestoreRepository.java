package com.choco_tur.choco_tur.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import java.util.*;
import java.util.concurrent.ExecutionException;

public abstract class FirestoreRepository<T> {

    private final Firestore firestore;
    private final String collection;

    private final ObjectMapper objectMapper;

    protected FirestoreRepository(Firestore firestore, String collection) {
        this.firestore = firestore;
        this.collection = collection;
        objectMapper = new ObjectMapper();
    }

    protected T findDocumentById(Class<T> classT, String id) throws ExecutionException, InterruptedException, NoSuchElementException {
        DocumentReference documentReference = firestore.collection(collection).document(id);
        DocumentSnapshot document = documentReference.get().get();
        if (!document.exists()) {
            throw new NoSuchElementException("Document with ID " + id + " was not found");
        }

        return objectMapper.convertValue(document.getData(), classT);
    }

    protected DocumentSnapshot findRawDocumentById(Class<T> classT, String id) throws ExecutionException, InterruptedException, NoSuchElementException {
        DocumentReference documentReference = firestore.collection(collection).document(id);
        DocumentSnapshot document = documentReference.get().get();
        if (!document.exists()) {
            throw new NoSuchElementException("Document with ID " + id + " was not found");
        }

        return document;
    }

    protected Map<String, Map<String, Object>> findAllDocumentsInSubCollection(String id, String subCollection) throws ExecutionException, InterruptedException, NoSuchElementException {
        Iterable<DocumentReference> it = firestore.collection(collection).document(id).collection(subCollection).listDocuments();

        Map<String, Map<String, Object>> ret = new HashMap<>();
        for (DocumentReference documentReference : it) {
            DocumentSnapshot documentSnapshot = documentReference.get().get();
            if (documentSnapshot.exists()) {
                Map<String, Object> data = documentSnapshot.getData();
                ret.put(documentSnapshot.getId(), data);
            }
        }

        return ret;
    }

    protected void save(T entity, String id) {
        Map<String, Object> map = objectMapper.convertValue(entity, Map.class);
        firestore.collection(collection).document(id).set(map);
    }

    protected Map<String, T> findAllDocuments(Class<T> classT) throws ExecutionException, InterruptedException {
        Iterable<DocumentReference> it = firestore.collection(collection).listDocuments();

        Map<String, T> ret = new HashMap<>();
        for (DocumentReference documentReference : it) {
            DocumentSnapshot documentSnapshot = documentReference.get().get();
            if (documentSnapshot.exists()) {
                Map<String, Object> data = documentSnapshot.getData();
                ret.put(documentSnapshot.getId(), objectMapper.convertValue(data, classT));
            }
        }

        return ret;
    }
}
