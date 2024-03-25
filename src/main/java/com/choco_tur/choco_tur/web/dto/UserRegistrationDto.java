package com.choco_tur.choco_tur.web.dto;

import com.choco_tur.choco_tur.utils.PasswordMatches;
import com.choco_tur.choco_tur.utils.ValidEmail;

import com.choco_tur.choco_tur.utils.ValidPassword;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@PasswordMatches
@Getter @Setter
public class UserRegistrationDto {
  @NotNull
  @NotEmpty
  @ValidEmail
  private String email;

  @NotNull
  @NotEmpty
  @ValidPassword
  private String password;

  @NotNull
  @NotEmpty
  @ValidPassword
  private String matchingPassword;

  private String dateOfBirth;

  private String nationality;
}
