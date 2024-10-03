package com.bstirbat.personalitytest.model;

import javax.validation.constraints.NotNull;

public class AnswerQuizModel {

    @NotNull
    private Integer variant;

    public Integer getVariant() {
        return variant;
    }

    public void setVariant(Integer variant) {
        this.variant = variant;
    }
}
