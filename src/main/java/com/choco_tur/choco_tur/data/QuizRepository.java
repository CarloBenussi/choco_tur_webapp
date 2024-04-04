package com.choco_tur.choco_tur.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutionException;

@Repository
public class QuizRepository extends FirestoreRepository<Quiz> {

    static final String QUIZ_COLLECTION_NAME = "quiz";

    private final ObjectMapper objectMapper;
    protected QuizRepository(Firestore firestore, ObjectMapper objectMapper) {
        super(firestore, QUIZ_COLLECTION_NAME);
        this.objectMapper = objectMapper;
    }

    public Quiz getQuiz(String quizId) throws ExecutionException, InterruptedException {
        Quiz quiz = findDocumentById(Quiz.class, quizId);
        quiz.setId(quizId);
        return quiz;
    }
}
