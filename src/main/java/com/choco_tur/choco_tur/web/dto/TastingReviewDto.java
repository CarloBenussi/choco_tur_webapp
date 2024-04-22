package com.choco_tur.choco_tur.web.dto;

import com.choco_tur.choco_tur.utils.ValidEmail;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TastingReviewDto {
    @NotNull
    @NotEmpty
    @ValidEmail
    String email;

    @NotNull
    @NotEmpty
    String tastingId;

    double score;
}
