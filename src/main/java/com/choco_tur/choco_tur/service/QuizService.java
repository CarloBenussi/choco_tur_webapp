package com.choco_tur.choco_tur.service;

import com.choco_tur.choco_tur.data.Quiz;
import com.choco_tur.choco_tur.data.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class QuizService {

    static final String WELCOME_QUIZ_ID = "welcomeQuiz";
    private final QuizRepository quizRepository;

    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    public Quiz getWelcomeQuiz() throws ExecutionException, InterruptedException {
        return quizRepository.getQuiz(WELCOME_QUIZ_ID);
    }
}
