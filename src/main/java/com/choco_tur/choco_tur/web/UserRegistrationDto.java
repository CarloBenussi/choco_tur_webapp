package com.choco_tur.choco_tur.web;

import com.choco_tur.choco_tur.utils.PasswordMatches;
import com.choco_tur.choco_tur.utils.ValidEmail;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@PasswordMatches
@Getter
public class UserRegistrationDto {
  @NotNull
  @NotEmpty
  private String password;
  private String matchingPassword;

  @NotNull
  @NotEmpty
  @ValidEmail
  private String email;
}
