package com.choco_tur.choco_tur.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class QuizRepository extends FirestoreRepository<Quiz> {

    static final String QUIZ_COLLECTION_NAME = "quizs";

    static final String QUIZ_QUESTIONS_COLLECTION_NAME = "questions";

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

    public List<QuizQuestion> getQuizQuestions(Quiz quiz) throws ExecutionException, InterruptedException {
        Map<String, Map<String, Object>> quizQuestionsData =
                findAllDocumentsInSubCollection(quiz.getId(), QUIZ_QUESTIONS_COLLECTION_NAME);
        List<QuizQuestion> quizQuestions = new ArrayList<>();
        for (String key : quizQuestionsData.keySet()) {
            QuizQuestion quizQuestion = objectMapper.convertValue(quizQuestionsData.get(key), QuizQuestion.class);
            quizQuestion.setIndex(Integer.parseInt(key));
            quizQuestions.add(quizQuestion);
        }
        return quizQuestions;
    }
}
