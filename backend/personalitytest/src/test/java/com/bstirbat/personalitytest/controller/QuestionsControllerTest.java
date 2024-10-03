package com.bstirbat.personalitytest.controller;

import com.bstirbat.personalitytest.entity.Question;
import com.bstirbat.personalitytest.exception.NotFoundException;
import com.bstirbat.personalitytest.exception.ValidationFailedException;
import com.bstirbat.personalitytest.model.CreateQuestionModel;
import com.bstirbat.personalitytest.model.CreateVariantModel;
import com.bstirbat.personalitytest.repository.QuestionRepository;
import com.bstirbat.personalitytest.repository.VariantRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class QuestionsControllerTest {
    private QuestionRepository questionRepository;
    private VariantRepository variantRepository;

    private QuestionsController questionsController;

    @BeforeEach
    public void init() {
        questionRepository = Mockito.mock(QuestionRepository.class);
        variantRepository = Mockito.mock(VariantRepository.class);

        questionsController = new QuestionsController(questionRepository, variantRepository);
    }

    @Test
    public void getById_NotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            questionsController.getById(1l);
        });
    }

    @Test
    public void getById_Ok() {
        Long questionId = 1l;

        Question question = new Question();
        question.setId(questionId);

        Mockito.doReturn(Optional.of(question)).when(questionRepository).findById(questionId);

        Question foundQuestion = questionsController.getById(questionId);

        assertEquals(questionId, foundQuestion.getId());
    }

    @Test
    public void createQuestion_NoVariants() {
        CreateQuestionModel createQuestionModel = new CreateQuestionModel();
        createQuestionModel.setVariants(Arrays.asList());

        Assertions.assertThrows(ValidationFailedException.class, () -> {
            questionsController.create(createQuestionModel);
        });
    }

    @Test
    public void createQuestion_Ok() {
        CreateVariantModel createVariantModel = new CreateVariantModel();
        createVariantModel.setBody("body");
        createVariantModel.setScore(1);
        createVariantModel.setPlaceInQuestion(1);

        CreateQuestionModel createQuestionModel = new CreateQuestionModel();
        createQuestionModel.setBody("body");
        createQuestionModel.setVariants(Arrays.asList(createVariantModel));

        Long questionId = 1l;
        Question question = new Question();
        question.setId(questionId);

        Mockito.doReturn(question).when(questionRepository).save(any());

        Question savedQuestion = questionsController.create(createQuestionModel);

        assertEquals(questionId, savedQuestion.getId());
    }

    @Test
    public void updateQuestion_NotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            questionsController.update(1l, new CreateQuestionModel());
        });
    }

    @Test
    public void updateQuestion_Ok() {
        Long questionId = 1l;
        Question question = new Question();
        question.setId(questionId);

        Mockito.doReturn(Optional.of(question)).when(questionRepository).findById(questionId);

        questionsController.update(questionId, new CreateQuestionModel());
    }

    @Test
    public void deleteQuestion_NotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            questionsController.delete(1l);
        });
    }

    @Test
    public void deleteQuestion_Ok() {
        Long questionId = 1l;
        Question question = new Question();
        question.setId(questionId);
        question.setVariants(Arrays.asList());

        Mockito.doReturn(Optional.of(question)).when(questionRepository).findById(questionId);

        questionsController.delete(questionId);
    }

}
