package com.bstirbat.personalitytest.controller;

import com.bstirbat.personalitytest.entity.ExtrovertResultThreshold;
import com.bstirbat.personalitytest.entity.Question;
import com.bstirbat.personalitytest.entity.Quiz;
import com.bstirbat.personalitytest.entity.Variant;
import com.bstirbat.personalitytest.exception.NotFoundException;
import com.bstirbat.personalitytest.exception.ValidationFailedException;
import com.bstirbat.personalitytest.model.AnswerQuizModel;
import com.bstirbat.personalitytest.repository.ExtrovertResultThresholdRepository;
import com.bstirbat.personalitytest.repository.QuestionRepository;
import com.bstirbat.personalitytest.repository.QuizRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class QuizControllerTest {

    private QuizRepository quizRepository;
    private QuestionRepository questionRepository;
    private ExtrovertResultThresholdRepository extrovertResultThresholdRepository;

    private QuizController quizController;

    @BeforeEach
    public void init() {
        quizRepository = Mockito.mock(QuizRepository.class);
        questionRepository = Mockito.mock(QuestionRepository.class);
        extrovertResultThresholdRepository = Mockito.mock(ExtrovertResultThresholdRepository.class);

        quizController = new QuizController(quizRepository, questionRepository, extrovertResultThresholdRepository);
    }

    @Test
    public void findById_NoQuizFound() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            quizController.findById("quizId");
        });
    }

    @Test
    public void findById_QuizFound() {
        Quiz quiz = new Quiz();
        quiz.setQuizId("quizId");

        Mockito.doReturn(quiz).when(quizRepository).findByQuizId("quizId");

        Quiz foundQuiz = quizController.findById("quizId");
        assertEquals("quizId", foundQuiz.getQuizId());
    }

    @Test
    public void quizCreate_NoQuestionsFound() {
        Assertions.assertThrows(ValidationFailedException.class, () -> {
            quizController.create();
        });
    }

    @Test
    public void quizCreate_Ok() {
        Question q1 = new Question();
        q1.setId(1l);

        Question q2 = new Question();
        q2.setId(2l);

        Quiz savedQuiz = new Quiz();
        savedQuiz.setQuizId("quizId");

        Mockito.doReturn(Arrays.asList(q1, q2)).when(questionRepository).findAll();
        Mockito.doReturn(savedQuiz).when(quizRepository).save(any());

        Quiz quiz = quizController.create();
        assertEquals("quizId", quiz.getQuizId());
    }

    @Test
    public void answerCurrentQuestion_NoQuizFound() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            quizController.answerCurrentQuestion("quizId", new AnswerQuizModel());
        });
    }

    @Test
    public void answerCurrentQuestion_CompletedQuiz() {
        String quizId = "quizId";

        Quiz quiz = new Quiz();
        quiz.setQuizId(quizId);
        quiz.setCompleted(true);

        Mockito.doReturn(quiz).when(quizRepository).findByQuizId(quizId);

        Assertions.assertThrows(ValidationFailedException.class, () -> {
            quizController.answerCurrentQuestion("quizId", new AnswerQuizModel());
        });
    }

    @Test
    public void answerCurrentQuestion_NoVariantsFound() {
        String quizId = "quizId";

        Variant v1 = new Variant();
        v1.setId(1l);
        v1.setPlaceInQuestion(1);

        Variant v2 = new Variant();
        v2.setId(2l);
        v2.setPlaceInQuestion(2);

        Question currentQuestion = new Question();
        currentQuestion.setVariants(Arrays.asList(v1, v2));

        Quiz quiz = new Quiz();
        quiz.setQuizId(quizId);
        quiz.setCurrentQuestion(currentQuestion);

        AnswerQuizModel answerQuizModel = new AnswerQuizModel();
        answerQuizModel.setVariant(3);

        Mockito.doReturn(quiz).when(quizRepository).findByQuizId(quizId);

        Assertions.assertThrows(ValidationFailedException.class, () -> {
            quizController.answerCurrentQuestion("quizId", answerQuizModel);
        });
    }

    @Test
    public void answerCurrentQuestion_NotCompleted() {
        String quizId = "quizId";

        Variant v1 = new Variant();
        v1.setId(1l);
        v1.setScore(1);
        v1.setPlaceInQuestion(1);

        Variant v2 = new Variant();
        v2.setId(2l);
        v2.setScore(2);
        v2.setPlaceInQuestion(2);

        Question q1 = new Question();
        q1.setId(1l);
        q1.setVariants(Arrays.asList(v1, v2));

        Question q2 = new Question();
        q2.setId(2l);
        q2.setVariants(Arrays.asList(v1, v2));

        Quiz quiz = new Quiz();
        quiz.setQuizId(quizId);
        quiz.setCurrentQuestion(q1);
        quiz.setCompleted(false);
        quiz.setCurrentScore(0);

        AnswerQuizModel answerQuizModel = new AnswerQuizModel();
        answerQuizModel.setVariant(1);

        Mockito.doReturn(quiz).when(quizRepository).findByQuizId(quizId);
        Mockito.doReturn(Arrays.asList(q1, q2)).when(questionRepository).findAll();

        quizController.answerCurrentQuestion("quizId", answerQuizModel);

        ArgumentCaptor<Quiz> quizArgumentCaptor = ArgumentCaptor.forClass(Quiz.class);

        Mockito.verify(quizRepository).save(quizArgumentCaptor.capture());

        Quiz capturedQuiz = quizArgumentCaptor.getValue();

        assertFalse(capturedQuiz.getCompleted());
        assertTrue(capturedQuiz.getCurrentScore() == 1);
    }

    @Test
    public void answerCurrentQuestion_Completed_NoThreshold() {
        String quizId = "quizId";

        Variant v1 = new Variant();
        v1.setId(1l);
        v1.setScore(1);
        v1.setPlaceInQuestion(1);

        Variant v2 = new Variant();
        v2.setId(2l);
        v2.setScore(2);
        v2.setPlaceInQuestion(2);

        Question q1 = new Question();
        q1.setId(1l);
        q1.setVariants(Arrays.asList(v1, v2));

        Question q2 = new Question();
        q2.setId(2l);
        q2.setVariants(Arrays.asList(v1, v2));

        Quiz quiz = new Quiz();
        quiz.setQuizId(quizId);
        quiz.setCurrentQuestion(q2);
        quiz.setCompleted(false);
        quiz.setCurrentScore(0);

        AnswerQuizModel answerQuizModel = new AnswerQuizModel();
        answerQuizModel.setVariant(1);

        Mockito.doReturn(quiz).when(quizRepository).findByQuizId(quizId);
        Mockito.doReturn(Arrays.asList(q1, q2)).when(questionRepository).findAll();

        Assertions.assertThrows(ValidationFailedException.class, () -> {
            quizController.answerCurrentQuestion("quizId", answerQuizModel);
        });
    }

    @Test
    public void answerCurrentQuestion_Completed() {
        String quizId = "quizId";

        Variant v1 = new Variant();
        v1.setId(1l);
        v1.setScore(1);
        v1.setPlaceInQuestion(1);

        Variant v2 = new Variant();
        v2.setId(2l);
        v2.setScore(2);
        v2.setPlaceInQuestion(2);

        Question q1 = new Question();
        q1.setId(1l);
        q1.setVariants(Arrays.asList(v1, v2));

        Question q2 = new Question();
        q2.setId(2l);
        q2.setVariants(Arrays.asList(v1, v2));

        Quiz quiz = new Quiz();
        quiz.setQuizId(quizId);
        quiz.setCurrentQuestion(q2);
        quiz.setCompleted(false);
        quiz.setCurrentScore(0);

        AnswerQuizModel answerQuizModel = new AnswerQuizModel();
        answerQuizModel.setVariant(1);

        ExtrovertResultThreshold threshold = new ExtrovertResultThreshold();
        threshold.setThreshold(2);

        Mockito.doReturn(quiz).when(quizRepository).findByQuizId(quizId);
        Mockito.doReturn(Arrays.asList(q1, q2)).when(questionRepository).findAll();
        Mockito.doReturn(Arrays.asList(threshold)).when(extrovertResultThresholdRepository).findAll();

        quizController.answerCurrentQuestion("quizId", answerQuizModel);

        ArgumentCaptor<Quiz> quizArgumentCaptor = ArgumentCaptor.forClass(Quiz.class);

        Mockito.verify(quizRepository).save(quizArgumentCaptor.capture());

        Quiz capturedQuiz = quizArgumentCaptor.getValue();

        assertTrue(capturedQuiz.getCompleted());
        assertTrue(capturedQuiz.getCurrentScore() == 1);
    }

    @Test
    public void deleteQuiz_NotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            quizController.deleteQuiz("quizId");
        });
    }

    @Test
    public void deleteQuiz_Ok() {
        String quizId = "quizId";

        Quiz quiz = new Quiz();
        quiz.setQuizId(quizId);

        Mockito.doReturn(quiz).when(quizRepository).findByQuizId(quizId);

        quizController.deleteQuiz(quizId);
    }
}
