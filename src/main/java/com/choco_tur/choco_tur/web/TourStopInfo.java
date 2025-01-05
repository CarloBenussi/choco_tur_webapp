package com.choco_tur.choco_tur.web;

import jakarta.persistence.ElementCollection;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

import com.choco_tur.choco_tur.data.TourStop;

@Getter
@Setter
public class TourStopInfo {
    TourStopInfo(TourStop tourStop) {
        titles = tourStop.getTitles();
        latitude = tourStop.getLatitude();
        longitude = tourStop.getLongitude();
    }

    @ElementCollection
    private Map<String, String> titles;

    private double latitude;

    private double longitude;
}