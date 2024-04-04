package com.choco_tur.choco_tur.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.choco_tur.choco_tur.data.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

@Service
public class TourService {
  private final TourRepository tourRepository;
  private final TourStopRepository tourStopRepository;

  public TourService(TourRepository tourRepository, TourStopRepository tourStopRepository) {
    this.tourRepository = tourRepository;
      this.tourStopRepository = tourStopRepository;
  }

  public List<Tour> getAllTours() throws ExecutionException, InterruptedException {
    List<Tour> tours = tourRepository.findAllTours();
    for (Tour tour : tours) {
      Object tourStopInfos = tourRepository.getTourStopInfos(tour);
      tour.setStopInfos(tourStopInfos);
    }

    for (Tour tour : tours) {
      Object tourTastingInfos = tourRepository.getTourTastingInfos(tour);
      tour.setTastingInfos(tourTastingInfos);
    }

    return tours;
  }

  public Tour getTour(String tourId) throws ExecutionException, InterruptedException {
    return tourRepository.getTour(tourId);
  }

  public List<TourStop> getTourStops(String tourId) throws ExecutionException, InterruptedException {
    Tour tour = tourRepository.getTour(tourId);
    List<TourStop> stops = new ArrayList<>();
    for (String stopId : tour.getStopIds()) {
      stops.add(tourStopRepository.getTourStop(stopId));
    }

    return stops;
  }

  public List<TourStopStory> getTourStopStories(String stopId) throws ExecutionException, InterruptedException, JsonProcessingException {
    return tourStopRepository.getTourStopStories(stopId);
  }
}
