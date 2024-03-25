package com.choco_tur.choco_tur.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class User {
  @Id
  private String email;

  private String password;

  private String emailVerificationNumber;

  private long emailVerificationNumberExpirationTime = -1;

  private boolean emailValidationStatus = false;

  private String passwordResetToken;

  private long passwordResetTokenGenerationTime = -1;

  private Integer externalProviderId = -1;

  private String dateOfBirth;

  private String nationality;
}
