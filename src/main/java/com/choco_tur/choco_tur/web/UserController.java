package com.choco_tur.choco_tur.web;

import com.choco_tur.choco_tur.data.UserLoginInfo;
import com.choco_tur.choco_tur.service.UserAlreadyExistAuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.choco_tur.choco_tur.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/registration")
  public ResponseEntity<String> registerUsername(@Valid @RequestBody UserRegistrationDto userDto) {
    try {
        UserLoginInfo registered = userService.registerNewUser(userDto);
    } catch (UserAlreadyExistAuthenticationException uaeEx) {
        return new ResponseEntity<>(uaeEx.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>("User successfully registered", HttpStatus.OK);
  }

}
