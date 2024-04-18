package com.choco_tur.choco_tur.data;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserQuizInfo {
    @NotEmpty
    private String id;

    private double progress;

    private double score;
}
