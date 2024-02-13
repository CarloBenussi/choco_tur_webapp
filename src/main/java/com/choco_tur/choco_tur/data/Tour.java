package com.choco_tur.choco_tur.data;

import java.sql.Time;
import java.util.List;
import java.util.Map;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Tour {

  @Id
  private String id;

  private String title;

  private float costEuros;

  private float lengthKm;

  private String avgDuration;

  @ElementCollection
  private Map<String, String> descriptions;

  private int numStops;

  private int numTastings;

  private String imageId;

  private Object stopInfos;
}
