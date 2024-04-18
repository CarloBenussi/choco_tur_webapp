package com.choco_tur.choco_tur.data;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter @Setter
@Entity
public class QuizQuestion {
    @Id
    private Integer index;

    @ElementCollection
    private Map<String, String> question;

    private Object answers;

    private Integer correctAnswerIndex;

    private Object onAnswers;
}
