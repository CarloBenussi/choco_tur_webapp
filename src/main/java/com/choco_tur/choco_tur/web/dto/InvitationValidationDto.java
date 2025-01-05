package com.choco_tur.choco_tur.web.dto;

import com.choco_tur.choco_tur.utils.ValidEmail;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InvitationValidationDto {
  @NotNull
  @NotEmpty
  @ValidEmail
  private String email;

  @NotNull
  @NotEmpty
  private String businessId;

  @NotNull
  @NotEmpty
  private String invitationToken;
}
