package com.choco_tur.choco_tur.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class TourStopStory {
    @Id
    private Integer index;

    private Integer type;

    private String contentJson;
}
