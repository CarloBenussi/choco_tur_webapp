package com.choco_tur.choco_tur.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserLoginResponse {
    @NotEmpty
    private String accessToken;

    @NotEmpty
    private String refreshToken;

    @Min(0)
    private long accessTokenExpiresIn;

    @Min(0)
    private long refreshTokenExpiresIn;
}
