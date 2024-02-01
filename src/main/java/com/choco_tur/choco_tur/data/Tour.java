package com.choco_tur.choco_tur.data;

import java.sql.Time;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="tours")
@Getter @Setter
public class Tour {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="TOUR_ID")
  private long id;

  @Column(name="TOUR_NAME")
  private String name;

  @Column(name="COST_EUROS")
  private float costEuros;

  @Column(name="LENGTH_KM")
  private float lengthKm;

  @Column(name="AVG_DURATION")
  private Time avgDuration;

  @Column(name="DESCRIPTION")
  private String description;

  @Column(name="STOPS_COUNT")
  private int stopsCount;

  @Column(name="TASTINGS_COUNT")
  private int tastingsCount;

  @Column(name="IMAGE_ID")
  private int imageId;
}
