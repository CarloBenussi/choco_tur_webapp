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

@Entity
@Table(name="user_login_info")
@Getter @Setter
public class UserLoginInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name="UserId")
  private int id;

  @Column(name="EmailAddress")
  private String email;

  @Column(name="Password")
  private String password;

  @Column(name="EmailVerificationToken")
  private String emailVerificationToken;

  @Column(name="EmailVerificationTokenGenerationTime")
  private Timestamp emailVerificationTokenGenerationTime;

  @Column(name="EmailValidationStatus")
  private boolean emailValidationStatus = false;

  @Column(name="PasswordRecoveryToken")
  private String passwordRecoveryToken;

  @Column(name="PasswordRecoveryTokenGenerationTime")
  private Timestamp passwordRecoveryTokenGenerationTime;

  @Column(name="ExternalProviderId")
  private int externalProviderId;

  @Column(name="ExternalProviderToken")
  private String externalProviderToken;
}
