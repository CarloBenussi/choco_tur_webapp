package com.choco_tur.choco_tur.data;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Entity
@Getter @Setter
public class Offer {
    @Id
    private String id;

    // 0-discount, 1-tasting.
    private int type;

    private Integer tokensCost;

    // In seconds.
    private long duration;

    @ElementCollection
    private Map<String, String> titles;

    @ElementCollection
    private Map<String, String> descriptions;

    @ElementCollection
    private Map<String, String> conditions;

    @ElementCollection
    private List<String> businessIds;

    // Null if offer is not a tasting (e.g. discount).
    private String tastingId;
}
