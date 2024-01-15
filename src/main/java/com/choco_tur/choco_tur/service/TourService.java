package com.choco_tur.choco_tur.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.choco_tur.choco_tur.data.Tour;
import com.choco_tur.choco_tur.data.TourRepository;

@Service
public class TourService {
  private final TourRepository tourRepository;

  public TourService(TourRepository tourRepository) {
    this.tourRepository = tourRepository;
  }

  public List<Tour> getAllToursInfo() {
    Iterable<Tour> toursIt = this.tourRepository.findAll();

    List<Tour> tours = new ArrayList<Tour>();
    toursIt.forEach(tour -> {
      tours.add(tour);
    });

    return tours;
  }
}
