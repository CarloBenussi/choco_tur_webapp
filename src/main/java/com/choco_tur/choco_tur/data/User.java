package com.choco_tur.choco_tur.data;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class UserLoginInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String email;

  private String password;

  private String emailVerificationNumber;

  private Timestamp emailVerificationNumberExpirationTime;

  private boolean emailValidationStatus = false;

  private String passwordResetToken;

  private Timestamp passwordResetTokenGenerationTime;

  private Integer externalProviderId;
}
