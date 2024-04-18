package com.choco_tur.choco_tur.service;

import com.choco_tur.choco_tur.data.Quiz;
import com.choco_tur.choco_tur.data.QuizQuestion;
import com.choco_tur.choco_tur.data.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class QuizService {

    static final String WELCOME_QUIZ_ID = "welcomeQuiz";
    private final QuizRepository quizRepository;

    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    public Quiz getWelcomeQuiz() throws ExecutionException, InterruptedException {
        Quiz quiz = quizRepository.getQuiz(WELCOME_QUIZ_ID);
        Object quizQuestions = quizRepository.getQuizQuestions(quiz);
        quiz.setQuestions(quizQuestions);
        return quiz;
    }

    public Quiz getQuiz(String quizId) throws ExecutionException, InterruptedException {
        Quiz quiz = quizRepository.getQuiz(quizId);
        Object quizQuestions = quizRepository.getQuizQuestions(quiz);
        quiz.setQuestions(quizQuestions);
        return quiz;
    }
}
