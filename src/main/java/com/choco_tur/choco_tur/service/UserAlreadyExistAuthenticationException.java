package com.choco_tur.choco_tur.service;

import javax.naming.AuthenticationException;

public class UserAlreadyExistAuthenticationException extends AuthenticationException {
    public UserAlreadyExistAuthenticationException (final String msg) {
      super(msg);
    }
}
