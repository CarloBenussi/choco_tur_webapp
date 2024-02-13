package com.choco_tur.choco_tur.web.dto;

import com.choco_tur.choco_tur.utils.PasswordMatches;
import com.choco_tur.choco_tur.utils.ValidEmail;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@PasswordMatches
@Getter @Setter
public class UserExtProviderSignInDto {
  @NotNull
  @NotEmpty
  @ValidEmail
  private String email;

  @NotNull
  @NotEmpty
  private String token;

  @Min(1)
  private int providerId;
}
