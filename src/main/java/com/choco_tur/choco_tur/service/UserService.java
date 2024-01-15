package com.choco_tur.choco_tur.service;

import com.choco_tur.choco_tur.data.UserLoginInfo;
import com.choco_tur.choco_tur.data.UserRepository;
import com.choco_tur.choco_tur.web.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserLoginInfo registerNewUser(UserRegistrationDto userDto) throws UserAlreadyExistAuthenticationException {
        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new UserAlreadyExistAuthenticationException("There is an account with that email address: "
                    + userDto.getEmail());
        }

        UserLoginInfo user = new UserLoginInfo();
        user.setEmail(userDto.getEmail());
        user.setPassword(encoder.encode(userDto.getPassword()));

        return userRepository.save(user);
    }
}
