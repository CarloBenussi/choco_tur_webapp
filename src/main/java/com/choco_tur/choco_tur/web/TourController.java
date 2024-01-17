package com.choco_tur.choco_tur.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tours")
public class TourController {
    @Autowired
    private MessageSource messageSource;

    @GetMapping("/getAllTours")
    @Secured("ROLE_USER")
    public ResponseEntity<Object> getAllAvailableTours() {

    }
}
