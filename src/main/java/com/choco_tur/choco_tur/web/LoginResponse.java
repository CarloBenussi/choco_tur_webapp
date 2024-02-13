package com.choco_tur.choco_tur.web;

import com.choco_tur.choco_tur.data.UserTourInfo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class LoginResponse {
    @NotEmpty
    private String accessToken;

    @NotEmpty
    private String refreshToken;

    @Min(0)
    private long accessTokenExpiresIn;

    @Min(0)
    private long refreshTokenExpiresIn;

    private List<UserTourInfo> tours;
}
