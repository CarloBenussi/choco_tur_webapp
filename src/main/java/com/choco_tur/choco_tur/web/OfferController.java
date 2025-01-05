package com.choco_tur.choco_tur.web;

import com.choco_tur.choco_tur.data.*;
import com.choco_tur.choco_tur.service.OfferService;
import com.choco_tur.choco_tur.service.UserService;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/offers")
public class OfferController {
    private final MessageSource messageSource;

    private final UserService userService;

    private final OfferService offerService;
    public OfferController(MessageSource messageSource, UserService userService, OfferService offerService) {
        this.messageSource = messageSource;
        this.userService = userService;
        this.offerService = offerService;
    }

    @GetMapping("/offers")
    public ResponseEntity<?> getOffers() throws ExecutionException, InterruptedException {

        List<Offer> offers = offerService.getAllOffers();

        return ResponseEntity.ok(offers);
    }
}
