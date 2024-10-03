package com.bstirbat.personalitytest.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CreateVariantModel {

    @NotBlank
    private String body;

    @NotNull
    private Integer placeInQuestion;

    @NotNull
    private Integer score;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getPlaceInQuestion() {
        return placeInQuestion;
    }

    public void setPlaceInQuestion(Integer placeInQuestion) {
        this.placeInQuestion = placeInQuestion;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
