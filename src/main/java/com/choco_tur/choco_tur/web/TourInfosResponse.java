package com.choco_tur.choco_tur.web;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class TourInfosResponse {
    @NotEmpty
    private List<TourInfoResponse> tours;
}
