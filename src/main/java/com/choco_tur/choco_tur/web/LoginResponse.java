package com.choco_tur.choco_tur.web;

import lombok.Builder;

@Builder
public class LoginResponse {
    private String token;

    private long expiresIn;
}
