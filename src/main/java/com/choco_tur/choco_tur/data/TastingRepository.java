package com.choco_tur.choco_tur.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutionException;

@Repository
public class TastingRepository extends FirestoreRepository<Tasting> {
    static final String TASTINGS_COLLECTION_NAME = "tastings";

    private final ObjectMapper objectMapper;

    public TastingRepository(Firestore firestore, ObjectMapper objectMapper) {
        super(firestore, TASTINGS_COLLECTION_NAME);
        this.objectMapper = objectMapper;
    }

    public Tasting getTasting(String tastingId) throws ExecutionException, InterruptedException {
        Tasting tasting = findDocumentById(Tasting.class, tastingId);
        tasting.setId(tastingId);
        return tasting;
    }

    public void save(Tasting tasting) {
        save(tasting, tasting.getId());
    }
}
