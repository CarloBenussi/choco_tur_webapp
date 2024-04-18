package com.choco_tur.choco_tur.web.dto;

import com.choco_tur.choco_tur.utils.NewPasswordMatches;
import com.choco_tur.choco_tur.utils.ValidEmail;
import com.choco_tur.choco_tur.utils.ValidPassword;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@NewPasswordMatches
@Getter @Setter
public class ChangePasswordDto {
  @NotNull
  @NotEmpty
  @ValidEmail
  private String email;

  @NotNull
  @NotEmpty
  private String passwordRecoveryNumber;

  @NotNull
  @NotEmpty
  @ValidPassword
  private String password;

  @NotNull
  @NotEmpty
  @ValidPassword
  private String matchingPassword;
}
