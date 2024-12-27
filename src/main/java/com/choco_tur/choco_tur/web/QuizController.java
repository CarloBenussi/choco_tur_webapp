package com.choco_tur.choco_tur.web;

import com.choco_tur.choco_tur.data.*;
import com.choco_tur.choco_tur.service.QuizService;
import com.choco_tur.choco_tur.service.UserService;
import com.choco_tur.choco_tur.web.dto.QuizScoreUpdateDto;
import com.choco_tur.choco_tur.web.dto.UserLoginWithTokenDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;
    private final UserService userService;

    public QuizController(QuizService quizService, UserService userService) {
        this.quizService = quizService;
        this.userService = userService;
    }

    @GetMapping("/welcome")
    public ResponseEntity<?> getWelcomeQuiz() throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        Quiz quiz = quizService.getWelcomeQuiz();

        return ResponseEntity.ok(quiz);
    }


    @GetMapping("/user/quizs")
    public ResponseEntity<?> getUserQuizs() throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userService.getUserByEmail(userDetails.getUsername());
        List<UserQuizInfo> userQuizInfos = userService.getUserQuizInfos(user);

        return ResponseEntity.ok(userQuizInfos);
    }

    @PostMapping("/user/updateQuizScore")
    public ResponseEntity<String> updateQuizScore(
            @RequestBody QuizScoreUpdateDto quizScoreUpdateDto
    ) throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.getUserByEmail(userDetails.getUsername());
        UserQuizInfo userQuizInfo = userService.getUserQuizInfo(user, quizScoreUpdateDto.getQuizId());
        if (userQuizInfo == null) {
            userQuizInfo = new UserQuizInfo();
            userQuizInfo.setId(quizScoreUpdateDto.getQuizId());
        }

        Quiz quiz = quizService.getQuiz(quizScoreUpdateDto.getQuizId());
        if (quiz == null) {
            return new ResponseEntity<>("Quiz " + quizScoreUpdateDto.getQuizId() + " does not exist",
                    HttpStatus.BAD_REQUEST);
        }

        userQuizInfo.setProgress(userQuizInfo.getProgress() + (double) 1 /(((ArrayList<?>) quiz.getQuestions()).size()));
        if (quizScoreUpdateDto.getCorrect()) {
            userQuizInfo.setScore(userQuizInfo.getScore() + (double) 1 / (((ArrayList<?>) quiz.getQuestions()).size()));
        }
        userService.saveUserQuiz(user, userQuizInfo);

        return ResponseEntity.ok("Upload successful!");
    }
}
