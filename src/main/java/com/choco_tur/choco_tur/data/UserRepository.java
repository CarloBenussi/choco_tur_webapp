package com.choco_tur.choco_tur.data;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<UserLoginInfo, Integer> {
    UserLoginInfo findByEmail(String email);
    UserLoginInfo findByEmailVerificationNumber(String number);
    UserLoginInfo findByPasswordResetToken(String passwordResetToken);
}
