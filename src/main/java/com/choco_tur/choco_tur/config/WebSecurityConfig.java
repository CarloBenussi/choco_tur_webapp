package com.choco_tur.choco_tur.config;

import com.choco_tur.choco_tur.data.UserDetailsServiceImpl;
import com.choco_tur.choco_tur.web.JwtAuthenticationFilter;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  @Autowired
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired
  private ConfigProperties configProperties;

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
      return http.getSharedObject(AuthenticationManagerBuilder.class)
              .authenticationProvider(authProvider())
              .build();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf()
            .disable()
            .authorizeHttpRequests()
            .requestMatchers("/users/info/**")
            .authenticated()
            .requestMatchers("/users/**")
            .permitAll()
            .requestMatchers("/tours/tours")
            .permitAll()
            .requestMatchers("/tours/**")
            .authenticated()
            .requestMatchers("/quiz/**")
            .authenticated()
            .requestMatchers("/tastings/**")
            .authenticated()
            .requestMatchers("/business/**")
            .hasRole("BUSINESS")
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authenticationProvider(authProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
  
  @Bean
  public static PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider authProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(encoder());
    return authProvider;
  }

  @Bean
  public GoogleIdTokenVerifier googleIdTokenVerifier() {
    return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
            .setAudience(Collections.singletonList(configProperties.getGoogleClientId()))
            .build();
  }
}
