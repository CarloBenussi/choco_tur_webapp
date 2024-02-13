package com.choco_tur.choco_tur.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class TourRepository extends FirestoreRepository<Tour> {

    static final String TOURS_COLLECTION_NAME = "tours";

    private final ObjectMapper objectMapper;
    protected TourRepository(Firestore firestore, ObjectMapper objectMapper) {
        super(firestore, TOURS_COLLECTION_NAME);
        this.objectMapper = objectMapper;
    }

    public List<Tour> findAllTours() throws ExecutionException, InterruptedException {
        Map<String, Tour> toursMap = findAllDocuments(Tour.class);
        // Transfer document ID inside tour object.
        for (String key : toursMap.keySet()) {
            toursMap.get(key).setId(key);
        }

        List<Tour> tours = new ArrayList<>();
        for (String key : toursMap.keySet()) {
            tours.add(toursMap.get(key));
        }
        return tours;
    }

    public List<TourStopInfo> getTourStopInfos(Tour tour) throws ExecutionException, InterruptedException {
        DocumentSnapshot userToursDataDoc =
                findRawDocumentById(Tour.class, tour.getId());
        List<Object> tourStopInfosData = (List<Object>) userToursDataDoc.get("stopInfos");
        List<TourStopInfo> tourStopInfos = new ArrayList<>();
        for (Object tourStopInfoData : tourStopInfosData) {
            tourStopInfos.add(objectMapper.convertValue(tourStopInfoData, TourStopInfo.class));
        }
        return tourStopInfos;
    }
}
