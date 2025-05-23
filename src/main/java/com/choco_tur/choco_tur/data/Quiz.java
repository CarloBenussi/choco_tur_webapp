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
public class Quiz {

    @Id
    private String id;

    @ElementCollection
    private Map<String, String> intro;

    private Object questions;
}
