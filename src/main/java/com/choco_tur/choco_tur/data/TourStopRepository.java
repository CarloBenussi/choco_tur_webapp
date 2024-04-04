package com.choco_tur.choco_tur.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class TourStopRepository extends FirestoreRepository<TourStop> {
    static final String TOUR_STOPS_COLLECTION_NAME = "stops";
    static final String TOUR_STOP_STORY_COLLECTION_NAME = "story";

    private final ObjectMapper objectMapper;
    protected TourStopRepository(Firestore firestore, ObjectMapper objectMapper) {
        super(firestore, TOUR_STOPS_COLLECTION_NAME);
        this.objectMapper = objectMapper;
    }

    public TourStop getTourStop(String stopId) throws ExecutionException, InterruptedException {
        TourStop tourStop = findDocumentById(TourStop.class, stopId);
        tourStop.setId(stopId);
        return tourStop;
    }

    public List<TourStopStory> getTourStopStories(String stopId) throws ExecutionException, InterruptedException, JsonProcessingException {
        Map<String, Map<String, Object>> tourStopStoriesData = findAllDocumentsInSubCollection(stopId, TOUR_STOP_STORY_COLLECTION_NAME);
        List<TourStopStory> tourStopStories = new ArrayList<>();
        for (String key : tourStopStoriesData.keySet()) {
            TourStopStory tourStopStory = new TourStopStory();
            tourStopStory.setIndex(Integer.parseInt(key));
            tourStopStory.setType(Integer.parseInt(tourStopStoriesData.get(key).get("type").toString()));
            tourStopStory.setContentJson(objectMapper.writeValueAsString(tourStopStoriesData.get(key)));
            tourStopStories.add(tourStopStory);
        }
        return tourStopStories;
    }
}
