package com.choco_tur.choco_tur.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class OfferRepository extends FirestoreRepository<Offer> {
    static final String OFFERS_COLLECTION_NAME = "offers";

    private final ObjectMapper objectMapper;

    public OfferRepository(Firestore firestore, ObjectMapper objectMapper) {
        super(firestore, OFFERS_COLLECTION_NAME);
        this.objectMapper = objectMapper;
    }

    public List<Offer> getAllOffers() throws ExecutionException, InterruptedException {
        Map<String, Offer> offersMap = findAllDocuments(Offer.class);
        // Transfer document ID inside offer object.
        for (String key : offersMap.keySet()) {
            offersMap.get(key).setId(key);
        }

        List<Offer> offers = new ArrayList<>();
        for (String key : offersMap.keySet()) {
            offers.add(offersMap.get(key));
        }
        return offers;
    }

    public Offer getOffer(String offerId) throws ExecutionException, InterruptedException {
        Offer offer = findDocumentById(Offer.class, offerId);
        offer.setId(offerId);
        return offer;
    }

    public void save(Offer offer) {
        save(offer, offer.getId());
    }
}
