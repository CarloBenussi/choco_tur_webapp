package com.choco_tur.choco_tur.web;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/business")
public class BusinessController {
    private final MessageSource messageSource;
    public BusinessController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping("/testEndpoint")
    public ResponseEntity<Object> testEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.isAuthenticated();
        // TODO: Implement.
        return ResponseEntity.ok("Success");
    }
}
