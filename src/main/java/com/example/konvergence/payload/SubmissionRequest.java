package com.example.konvergence.payload;
import javax.validation.constraints.NotNull;

public class SubmissionRequest {
    @NotNull
    private Long choiceId;

    public Long getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(Long choiceId) {
        this.choiceId = choiceId;
    }
}

