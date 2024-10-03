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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/quizzes")
@CrossOrigin(origins = "*")
public class QuizController {

    private static final String INTROVERT = "INTROVERT";
    private static final String EXTROVERT = "EXTROVERT";

    private QuizRepository quizRepository;
    private QuestionRepository questionRepository;
    private ExtrovertResultThresholdRepository extrovertResultThresholdRepository;

    @Autowired
    public QuizController(QuizRepository quizRepository,
                          QuestionRepository questionRepository,
                          ExtrovertResultThresholdRepository extrovertResultThresholdRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.extrovertResultThresholdRepository = extrovertResultThresholdRepository;
    }

    @GetMapping
    public List<Quiz> all() {

        return quizRepository.findAll();
    }

    @GetMapping("/{quizId}")
    public Quiz findById(@PathVariable String quizId) {

        Quiz quiz = quizRepository.findByQuizId(quizId);
        if (quiz == null) {
            throw new NotFoundException("Cannot find quiz with quizId=" + quizId);
        }

        return quiz;
    }

    @PostMapping
    public Quiz create() {
        List<Question> questions = retrieveAll();
        if (questions == null || questions.size() == 0) {
            throw new ValidationFailedException("Cannot create a quiz if there are no questions.");
        }

        Quiz quiz = new Quiz();
        quiz.setQuizId(UUID.randomUUID().toString());
        quiz.setCurrentQuestion(questions.get(0));
        quiz.setCurrentScore(0);
        quiz.setCompleted(false);

        quiz = quizRepository.save(quiz);

        return quiz;
    }

    @PutMapping("/{quizId}")
    public Quiz answerCurrentQuestion(@PathVariable String quizId, @Valid @RequestBody AnswerQuizModel answerQuizModel) {

        Quiz quiz = quizRepository.findByQuizId(quizId);
        if (quiz == null) {
            throw new NotFoundException("Cannot find quiz with quizId=" + quizId);
        }

        if (quiz.getCompleted() != null && quiz.getCompleted()) {
            throw new ValidationFailedException("Cannot answer a finished quiz");
        }

        Question currentQuestion = quiz.getCurrentQuestion();

        Variant variant = findVariant(currentQuestion.getVariants(), answerQuizModel.getVariant());
        if (variant == null) {
            throw new ValidationFailedException(String.format("Question with id %s doesn't has variant %s", currentQuestion.getId(), answerQuizModel.getVariant()));
        }

        List<Question> questions = retrieveAll();
        int currentPosition = findPosition(questions, currentQuestion);

        quiz.setCurrentScore(quiz.getCurrentScore() + variant.getScore());

        if (currentPosition == questions.size() - 1) {
            completeQuiz(quiz);
        } else {
            quiz.setCurrentQuestion(questions.get(currentPosition + 1));
        }

        quiz = quizRepository.save(quiz);

        return quiz;
    }

    @DeleteMapping("/{quizId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuiz(@PathVariable String quizId) {

        Quiz quiz = quizRepository.findByQuizId(quizId);
        if (quiz == null) {
            throw new NotFoundException("Cannot find quiz with quizId=" + quizId);
        }

        quizRepository.delete(quiz);
    }

    private List<Question> retrieveAll() {
        List<Question> questions = questionRepository.findAll();

        Collections.sort(questions, (q1, q2) -> {
            if (q1.getId() > q2.getId()) return 1;
            if (q1.getId() < q2.getId()) return -1;
            return 0;
        });

        return questions;
    }

    private Variant findVariant(List<Variant> variants, Integer placeInQuestion) {
        for (Variant variant: variants) {
            if (variant.getPlaceInQuestion() == placeInQuestion) {
                return variant;
            }
        }

        return null;
    }

    private int findPosition(List<Question> questions, Question currentQuestion) {
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getId() == currentQuestion.getId()) {
                return i;
            }
        }

        return -1;
    }

    private void completeQuiz(Quiz quiz) {
        List<ExtrovertResultThreshold> thresholds = extrovertResultThresholdRepository.findAll();
        if (thresholds == null || thresholds.size() != 1) {
            throw new ValidationFailedException("Exactly one threshold must be stored in the DB");
        }

        ExtrovertResultThreshold threshold = thresholds.get(0);

        quiz.setCompleted(true);
        quiz.setResult(quiz.getCurrentScore() >= threshold.getThreshold()? EXTROVERT: INTROVERT);
    }
}
