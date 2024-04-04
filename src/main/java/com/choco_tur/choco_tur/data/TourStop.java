package com.choco_tur.choco_tur.data;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Entity
@Getter
@Setter
public class TourStop {
    @Id
    private String id;

    @ElementCollection
    private Map<String, String> titles;

    @ElementCollection
    private Map<String, String> descriptions;

    private double latitude;

    private double longitude;

    @NotEmpty
    private String imageId;

    @NotEmpty
    private String audioId;
}
