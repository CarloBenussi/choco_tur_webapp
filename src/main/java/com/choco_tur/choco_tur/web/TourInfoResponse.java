package com.choco_tur.choco_tur.web;

import java.util.List;

import com.choco_tur.choco_tur.data.Tour;
import com.choco_tur.choco_tur.web.TourStopInfo;
import com.choco_tur.choco_tur.web.TourTastingInfo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TourInfoResponse {
    @NotNull
    private Tour tour;

    @NotEmpty
    private List<TourStopInfo> stopInfos;

    @NotEmpty
    private List<TourTastingInfo> tastingInfos;
}
