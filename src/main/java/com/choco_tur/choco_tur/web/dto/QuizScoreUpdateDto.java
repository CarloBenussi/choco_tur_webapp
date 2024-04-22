package com.choco_tur.choco_tur.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizScoreUpdateDto {
    @NotEmpty
    private String quizId;

    @Min(0)
    private int questionIndex;

    private Boolean correct;
}
