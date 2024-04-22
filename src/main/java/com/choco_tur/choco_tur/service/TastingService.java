package com.choco_tur.choco_tur.service;

import com.choco_tur.choco_tur.data.Tasting;
import com.choco_tur.choco_tur.data.TastingRepository;
import com.choco_tur.choco_tur.data.User;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class TastingService {

    private final TastingRepository tastingRepository;

    public TastingService(TastingRepository tastingRepository) {
        this.tastingRepository = tastingRepository;
    }

    public Tasting getTasting(String tastingId) throws ExecutionException, InterruptedException {
        return tastingRepository.getTasting(tastingId);
    }

    public void updateTastingScore(String tastingId, double score) throws ExecutionException, InterruptedException {
        Tasting tasting = tastingRepository.getTasting(tastingId);
        tasting.getReviews().add(score);
        saveTasting(tasting);
    }

    public void saveTasting(Tasting tasting) {
        tastingRepository.save(tasting);
    }
}
