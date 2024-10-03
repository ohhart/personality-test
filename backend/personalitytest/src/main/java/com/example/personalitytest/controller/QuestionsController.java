package com.bstirbat.personalitytest.controller;

import com.bstirbat.personalitytest.entity.Question;
import com.bstirbat.personalitytest.entity.Variant;
import com.bstirbat.personalitytest.exception.NotFoundException;
import com.bstirbat.personalitytest.exception.ValidationFailedException;
import com.bstirbat.personalitytest.model.CreateQuestionModel;
import com.bstirbat.personalitytest.model.CreateVariantModel;
import com.bstirbat.personalitytest.repository.QuestionRepository;
import com.bstirbat.personalitytest.repository.VariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/questions")
@CrossOrigin(origins = "*")
public class QuestionsController {

    private QuestionRepository questionRepository;
    private VariantRepository variantRepository;

    @Autowired
    public QuestionsController(QuestionRepository questionRepository, VariantRepository variantRepository) {
        this.questionRepository = questionRepository;
        this.variantRepository = variantRepository;
    }

    @GetMapping
    public List<Question> all() {

        return questionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Question getById(@PathVariable Long id) {

        return questionRepository.findById(id).orElseThrow(() -> new NotFoundException("Could not find a question with id=" + id));
    }

    @PostMapping
    public Question create(@Valid @RequestBody CreateQuestionModel createQuestionModel) {
        if (createQuestionModel.getVariants() == null || createQuestionModel.getVariants().isEmpty()) {
            throw new ValidationFailedException("The question needs to have variants");
        }

        Question question = new Question();
        question.setBody(createQuestionModel.getBody());

        question = questionRepository.save(question);

        List<Variant> variants = saveVariants(question, createQuestionModel.getVariants());
        question.setVariants(variants);

        return question;
    }

    @PutMapping("/{id}")
    public Question update(@PathVariable Long id, @RequestBody CreateQuestionModel createQuestionModel) {

        Question question = questionRepository.findById(id).orElseThrow(() -> new NotFoundException("Could not find a question with id=" + id));

        if (createQuestionModel.getBody() != null && createQuestionModel.getBody().length() > 0) {
            question.setBody(createQuestionModel.getBody());
            question = questionRepository.save(question);
        }

        if (createQuestionModel.getVariants() != null && createQuestionModel.getVariants().size() > 0) {
            for(Variant variant: question.getVariants()) {
                variantRepository.delete(variant);
            }

            question.setVariants(null);
            List<Variant> variants = saveVariants(question, createQuestionModel.getVariants());
            question.setVariants(variants);
        }

        return question;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Question question = questionRepository.findById(id).orElseThrow(() -> new NotFoundException("Could not find a question with id=" + id));

        for(Variant variant: question.getVariants()) {
            variantRepository.delete(variant);
        }

        questionRepository.delete(question);
    }

    private List<Variant> saveVariants(Question question, List<CreateVariantModel> variantModels) {
        List<Variant> variants = new ArrayList<>();

        for(CreateVariantModel createVariantModel: variantModels) {
            Variant variant = new Variant();
            variant.setBody(createVariantModel.getBody());
            variant.setPlaceInQuestion(createVariantModel.getPlaceInQuestion());
            variant.setScore(createVariantModel.getScore());
            variant.setQuestion(question);
            variant = variantRepository.save(variant);
            variants.add(variant);
        }

        return variants;
    }

}
