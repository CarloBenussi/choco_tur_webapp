package com.choco_tur.choco_tur.web.dto;

import com.choco_tur.choco_tur.utils.ValidEmail;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserEmailWithTokenDto {
    @NotEmpty
    @ValidEmail
    private String email;

    @NotEmpty
    private String token;
}
