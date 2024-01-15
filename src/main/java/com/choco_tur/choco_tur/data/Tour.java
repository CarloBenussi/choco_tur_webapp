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
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name="TourId")
  private int id;

  @Column(name="TourName")
  private String name;

  @Column(name="CostEuros")
  private float costEuros;

  @Column(name="LengthKm")
  private float lengthKm;

  @Column(name="AvgDuration")
  private Time avgDuration;

  @Column(name="Description")
  private String description;

  @Column(name="StopsCount")
  private int stopsCount;

  @Column(name="TastingsCount")
  private int tastingsCount;

  @Column(name="ImageId")
  private int imageId;
}
