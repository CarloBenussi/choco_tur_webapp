package com.choco_tur.choco_tur.web.dto;

import com.choco_tur.choco_tur.utils.ValidEmail;
import com.choco_tur.choco_tur.utils.ValidPassword;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginDto {
    @NotEmpty
    @ValidEmail
    private String email;

    @NotEmpty
    @ValidPassword
    private String password;
}
