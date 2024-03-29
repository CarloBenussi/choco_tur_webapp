package com.choco_tur.choco_tur.data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
public class UserTourInfo {
    @NotEmpty
    private String purchaseDate;

    @NotEmpty
    private String expiryDate;

    @Min(0)
    private Integer purchaseMethod;

    @NotEmpty
    private String id;

    @NotEmpty
    private String title;

    private String nextStopId;

    private double progress;

    private boolean isActive;
}
