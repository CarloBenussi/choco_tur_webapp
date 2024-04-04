package com.choco_tur.choco_tur.web;

import com.choco_tur.choco_tur.data.*;
import com.choco_tur.choco_tur.service.TourService;
import com.choco_tur.choco_tur.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/tours")
public class TourController {
    private final MessageSource messageSource;

    private final UserService userService;

    private final TourService tourService;
    public TourController(MessageSource messageSource, UserService userService, TourService tourService) {
        this.messageSource = messageSource;
        this.userService = userService;
        this.tourService = tourService;
    }

    @GetMapping("/userTours")
    public ResponseEntity<?> getUserTours() throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userService.getUserByEmail(userDetails.getUsername());
        List<UserTourInfo> userTourInfos = userService.getUserTourInfos(user);

        return ResponseEntity.ok(userTourInfos);
    }

    @PostMapping("/activateUserTour")
    public ResponseEntity<?> activateUserTour(@RequestBody String tourId) throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userService.getUserByEmail(userDetails.getUsername());
        UserTourInfo userTourInfo = userService.getUserTourInfo(user, tourId);
        if (userTourInfo != null) {
            if (userTourInfo.isActive()) {
                return new ResponseEntity<>("User tour " + tourId + " is already active", HttpStatus.OK);
            }
        } else {
            Tour tour = tourService.getTour(tourId);
            if (tour == null) {
                return new ResponseEntity<>("Tour " + tourId + " does not exist", HttpStatus.BAD_REQUEST);
            }

            userTourInfo = new UserTourInfo();
            userTourInfo.setId(tour.getId());
            userTourInfo.setTitle(tour.getTitle());
            userTourInfo.setProgress(0);
        }

        userTourInfo.setActive(true);
        userTourInfo.setNextStopId(tourService.getTour(tourId).getStopIds().get(0));
        userService.saveUserTour(user, userTourInfo);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/deactivateUserTour")
    public ResponseEntity<?> deactivateUserTour(@RequestBody String tourId) throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userService.getUserByEmail(userDetails.getUsername());
        UserTourInfo userTourInfo = userService.getUserTourInfo(user, tourId);
        if (userTourInfo == null) {
            return new ResponseEntity<>("No user tour found with id " + tourId, HttpStatus.BAD_REQUEST);
        }

        if (!userTourInfo.isActive()) {
            return new ResponseEntity<>("User tour " + tourId + " is already inactive", HttpStatus.OK);
        }

        userTourInfo.setActive(false);
        userTourInfo.setNextStopId("");
        userTourInfo.setProgress(0);
        userService.saveUserTour(user, userTourInfo);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/advanceUserTour")
    public ResponseEntity<?> advanceUserTour(@RequestBody String tourId) throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userService.getUserByEmail(userDetails.getUsername());
        UserTourInfo userTourInfo = userService.getUserTourInfo(user, tourId);
        if (userTourInfo == null) {
            return new ResponseEntity<>("No user tour found with id " + tourId, HttpStatus.BAD_REQUEST);
        }

        if (!userTourInfo.isActive()) {
            return new ResponseEntity<>("User tour " + tourId + " is inactive", HttpStatus.BAD_REQUEST);
        }

        List<String> tourStopIds = tourService.getTour(tourId).getStopIds();
        int nextStopIndex = tourStopIds.indexOf(userTourInfo.getNextStopId());
        if (++nextStopIndex == tourStopIds.size()) {
            userTourInfo.setActive(false);
            userTourInfo.setNextStopId("");
            userService.saveUserTour(user, userTourInfo);
            return new ResponseEntity<>("User tour " + tourId + " reached the end: deactivated", HttpStatus.OK);
        } else {
            userTourInfo.setNextStopId(tourStopIds.get(nextStopIndex));
            userTourInfo.setProgress((double) nextStopIndex / tourStopIds.size());
            userService.saveUserTour(user, userTourInfo);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @PostMapping("/revertUserTour")
    public ResponseEntity<?> revertUserTour(@RequestBody String tourId) throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userService.getUserByEmail(userDetails.getUsername());
        UserTourInfo userTourInfo = userService.getUserTourInfo(user, tourId);
        if (userTourInfo == null) {
            return new ResponseEntity<>("No user tour found with id " + tourId, HttpStatus.BAD_REQUEST);
        }

        if (!userTourInfo.isActive()) {
            return new ResponseEntity<>("User tour " + tourId + " is inactive", HttpStatus.BAD_REQUEST);
        }

        List<String> tourStopIds = tourService.getTour(tourId).getStopIds();
        int nextStopIndex = tourStopIds.indexOf(userTourInfo.getNextStopId());
        if (--nextStopIndex < 0) {
            return new ResponseEntity<>("User tour " + tourId + " is already at the first stop", HttpStatus.OK);
        } else {
            userTourInfo.setNextStopId(tourStopIds.get(nextStopIndex));
            userTourInfo.setProgress((double) nextStopIndex / tourStopIds.size());
            userService.saveUserTour(user, userTourInfo);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @GetMapping("/tours")
    public ResponseEntity<?> getTours() throws ExecutionException, InterruptedException {

        List<Tour> tours = tourService.getAllTours();
        TourInfosResponse tourInfosResponse = TourInfosResponse.builder().tours(tours).build();

        return ResponseEntity.ok(tourInfosResponse);
    }

    @GetMapping("/tour")
    public ResponseEntity<?> getTour(@RequestParam String tourId) throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        Tour tour = tourService.getTour(tourId);
        return ResponseEntity.ok(tour);
    }

    @GetMapping("/tourStops")
    public ResponseEntity<?> getTourStops(@RequestParam String tourId) throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        // Check if user has purchased tour (if it is not free).
        Tour tour = tourService.getTour(tourId);
        if (tour.getCostEuros() > 0) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.getUserByEmail(userDetails.getUsername());
            List<UserTourInfo> userTourInfos = userService.getUserTourInfos(user);
            boolean tourPurchased = false;
            for (UserTourInfo userTourInfo : userTourInfos) {
                if (userTourInfo.getId().equals(tourId)) {
                    tourPurchased = true;
                }
            }
            if (!tourPurchased) {
                return new ResponseEntity<>("User has not purchaed tour " + tourId, HttpStatus.FORBIDDEN);
            }
        }

        List<TourStop> tourStops = tourService.getTourStops(tourId);
        return ResponseEntity.ok(tourStops);
    }

    @GetMapping("/tourStopStories")
    public ResponseEntity<?> getTourStopsStories(@RequestParam String stopId) throws ExecutionException, InterruptedException, JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        List<TourStopStory> tourStopStories = tourService.getTourStopStories(stopId);
        return ResponseEntity.ok(tourStopStories);
    }
}
