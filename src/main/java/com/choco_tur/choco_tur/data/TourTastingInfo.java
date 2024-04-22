package com.choco_tur.choco_tur.data;

import jakarta.persistence.ElementCollection;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class TourTastingInfo {
    @ElementCollection
    private Map<String, String> titles;

    private String imageId;
}
