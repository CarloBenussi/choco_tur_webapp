package com.choco_tur.choco_tur.data;

import jakarta.transaction.Transactional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository, BusinessRepository businessRepository) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException, RuntimeException {
        try {
            User user;
            user = userRepository.findByEmail(email);
            if (user != null) {
                boolean accountNonExpired = true;
                boolean credentialsNonExpired = true;
                boolean accountNonLocked = true;

                if (user.getPassword() != null) {
                    return new org.springframework.security.core.userdetails.User(
                            user.getEmail(), user.getPassword(), user.isEmailValidationStatus(), accountNonExpired,
                            credentialsNonExpired, accountNonLocked, getAuthorities(List.of("ROLE_USER")));
                } else if (user.getExternalProviderId() != null) {
                    // TODO: Here we need to (see 'AbstractUserDetailsAuthenticationProvider::authenticate').
                    return new org.springframework.security.core.userdetails.User(
                            user.getEmail(), null, user.isEmailValidationStatus(), accountNonExpired,
                            credentialsNonExpired, accountNonLocked, getAuthorities(List.of("ROLE_USER")));
                } else {
                    throw new UsernameNotFoundException("User with email " + email + " has no password nor external provider set.");
                }
            }

            // If we got here, it means there was no user with the given email: try to find a business then.
            Business business;
            business = businessRepository.findByEmail(email);
            if (business != null) {
                boolean accountNonExpired = true;
                boolean credentialsNonExpired = true;
                boolean accountNonLocked = true;

                if (business.getPassword() != null) {
                    return new org.springframework.security.core.userdetails.User(
                        business.getEmail(), business.getPassword(), business.isEmailValidationStatus(), accountNonExpired,
                            credentialsNonExpired, accountNonLocked, getAuthorities(List.of("ROLE_BUSINESS")));
                } else {
                    throw new UsernameNotFoundException("Business with email " + email + " has no password set.");
                }
            }

            throw new UsernameNotFoundException("No user nor business found with email: " + email);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failure with firebase: " + e.getCause().toString());
        }
    }

    private static List<GrantedAuthority> getAuthorities(List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
}
