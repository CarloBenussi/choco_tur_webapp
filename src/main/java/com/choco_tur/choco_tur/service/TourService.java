package com.choco_tur.choco_tur.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.choco_tur.choco_tur.data.*;
import com.choco_tur.choco_tur.web.TourStopInfo;
import com.choco_tur.choco_tur.web.TourTastingInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

@Service
public class TourService {
  private final TourRepository tourRepository;
  private final TourStopRepository tourStopRepository;
  private final TastingService tastingService;

  public TourService(TourRepository tourRepository, TourStopRepository tourStopRepository, TastingService tastingService) {
    this.tourRepository = tourRepository;
    this.tourStopRepository = tourStopRepository;
    this.tastingService = tastingService;
  }

  public List<Tour> getAllTours() throws ExecutionException, InterruptedException {
    List<Tour> tours = tourRepository.findAllTours();
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

  public List<Tasting> getTourTastings(String tourId) throws ExecutionException, InterruptedException {
    Tour tour = tourRepository.getTour(tourId);
    List<Tasting> tastings = new ArrayList<>();
    for (String tastingId : tour.getTastingIds()) {
      tastings.add(tastingService.getTasting(tastingId));
    }

    return tastings;
  }

  public List<TourStopStory> getTourStopStories(String stopId) throws ExecutionException, InterruptedException, JsonProcessingException {
    return tourStopRepository.getTourStopStories(stopId);
  }
}
