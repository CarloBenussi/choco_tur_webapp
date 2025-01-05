package com.choco_tur.choco_tur.service;

import com.choco_tur.choco_tur.data.Offer;
import com.choco_tur.choco_tur.data.OfferRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class OfferService {

    private final OfferRepository offerRepository;

    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    public List<Offer> getAllOffers() throws ExecutionException, InterruptedException {
        return this.offerRepository.getAllOffers();
    }

    public Offer getOffer(String offerId) throws ExecutionException, InterruptedException {
        return offerRepository.getOffer(offerId);
    }

    public void saveOffer(Offer offer) {
        offerRepository.save(offer);
    }
}
