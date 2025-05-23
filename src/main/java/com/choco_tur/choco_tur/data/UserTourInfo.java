package com.choco_tur.choco_tur.data;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTourInfo {
    private String purchaseDate;

    private String expiryDate;

    private Integer purchaseMethod;

    @NotEmpty
    private String id;

    @NotEmpty
    private String title;

    private String nextStopId;

    private double progress;

    private boolean isActive;
}
