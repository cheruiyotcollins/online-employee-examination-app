package com.examination.gigster.model;

public class ChoiceQuestionCount {
    private Long choiceId;
    private Long submissionCount;

    public ChoiceQuestionCount(Long choiceId, Long submissionCount) {
        this.choiceId = choiceId;
        this.submissionCount = submissionCount;
    }

    public Long getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(Long choiceId) {
        this.choiceId = choiceId;
    }

    public Long getSubmissionCount() {
        return submissionCount;
    }

    public void setSubmissionCount(Long submissionCount) {
        this.submissionCount = submissionCount;
    }
}

