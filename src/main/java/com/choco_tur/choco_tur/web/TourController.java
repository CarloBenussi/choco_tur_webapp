package com.choco_tur.choco_tur.web;

import com.choco_tur.choco_tur.data.Tour;
import com.choco_tur.choco_tur.service.TourService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/tours")
public class TourController {
    private final MessageSource messageSource;

    private final TourService tourService;
    public TourController(MessageSource messageSource, TourService tourService) {
        this.messageSource = messageSource;
        this.tourService = tourService;
    }

    @GetMapping("/toursInfo")
    //@Secured("ROLE_USER") // TODO: Needed given SecurityFilterChain??
    public ResponseEntity<?> getToursInfo() throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        List<Tour> tours = tourService.getAllToursInfo();
        TourInfosResponse tourInfosResponse = TourInfosResponse.builder().tours(tours).build();

        return ResponseEntity.ok(tourInfosResponse);
    }
}
