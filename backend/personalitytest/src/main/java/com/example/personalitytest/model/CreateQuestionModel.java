package com.bstirbat.personalitytest.model;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

public class CreateQuestionModel {

    @NotBlank
    private String body;

    @Valid
    private List<CreateVariantModel> variants;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<CreateVariantModel> getVariants() {
        return variants;
    }

    public void setVariants(List<CreateVariantModel> variants) {
        this.variants = variants;
    }
}
