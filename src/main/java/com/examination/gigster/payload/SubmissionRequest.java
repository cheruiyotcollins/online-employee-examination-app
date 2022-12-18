package com.examination.gigster.payload;
import javax.validation.constraints.NotNull;

public class SubmissionRequest {
    @NotNull
    private Long choiceId;
//    @NotNull
    private Long questionId;

    public Long getChoiceId() {
        return choiceId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }


    public void setChoiceId(Long choiceId) {
        this.choiceId = choiceId;
    }
}

