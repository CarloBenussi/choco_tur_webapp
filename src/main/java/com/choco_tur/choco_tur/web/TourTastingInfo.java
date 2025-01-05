package com.choco_tur.choco_tur.web;

import jakarta.persistence.ElementCollection;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

import com.choco_tur.choco_tur.data.Tasting;

@Getter
@Setter
public class TourTastingInfo {
    TourTastingInfo(Tasting tasting) {
        titles = tasting.getTitles();
        imageId = tasting.getImageId();
    }

    @ElementCollection
    private Map<String, String> titles;

    private String imageId;
}
