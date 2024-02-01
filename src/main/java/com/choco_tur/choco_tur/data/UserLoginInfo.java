package com.choco_tur.choco_tur.data;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name="user_login_info")
public class UserLoginInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="USER_ID")
  private long id;

  @Column(name="EMAIL_ADDRESS")
  private String email;

  @Column(name="PASSWORD")
  private String password;

  @Column(name="EMAIL_VERIFICATION_NUMBER")
  private String emailVerificationNumber;

  @Column(name="EMAIL_VERIFICATION_NUMBER_EXPIRATION_TIME")
  private Timestamp emailVerificationNumberExpirationTime;

  @Column(name="EMAIL_VALIDATION_STATUS")
  private boolean emailValidationStatus = false;

  @Column(name="PASSWORD_RESET_TOKEN")
  private String passwordResetToken;

  @Column(name="PASSWORD_RESET_TOKEN_EXPIRATION_TIME")
  private Timestamp passwordResetTokenGenerationTime;

  @Column(name="EXTERNAL_PROVIDER_ID")
  private Integer externalProviderId;
}
