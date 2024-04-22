package com.choco_tur.choco_tur.web;

import com.choco_tur.choco_tur.data.Tasting;
import com.choco_tur.choco_tur.data.User;
import com.choco_tur.choco_tur.data.UserTastingInfo;
import com.choco_tur.choco_tur.service.TastingService;
import com.choco_tur.choco_tur.service.UserService;
import com.choco_tur.choco_tur.web.dto.TastingReviewDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/tastings")
public class TastingController {

    private final TastingService tastingService;

    private final UserService userService;

    public TastingController(TastingService tastingService, UserService userService) {
        this.tastingService = tastingService;
        this.userService = userService;
    }

    @GetMapping("/tasting")
    public ResponseEntity<?> getTasting(@RequestParam String tastingId) throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        Tasting tasting = tastingService.getTasting(tastingId);

        return ResponseEntity.ok(tasting);
    }

    @PostMapping("/review")
    public ResponseEntity<?> reviewTasting(@RequestBody TastingReviewDto tastingReviewDto) throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.getUserByEmail(userDetails.getUsername());

        tastingService.updateTastingScore(tastingReviewDto.getTastingId(), tastingReviewDto.getScore());

        UserTastingInfo userTastingInfo = new UserTastingInfo();
        userTastingInfo.setId(tastingReviewDto.getTastingId());
        userTastingInfo.setScore(tastingReviewDto.getScore());
        userService.saveUserTasting(user, userTastingInfo);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
