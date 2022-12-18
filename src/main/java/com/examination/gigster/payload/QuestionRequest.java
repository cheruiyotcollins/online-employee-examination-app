package com.examination.gigster.payload;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class QuestionRequest {
    @NotBlank
    @Size(max = 140)
    private String question;

    @NotNull
    @Size(min = 2, max = 6)
    @Valid
    private List<ChoiceRequest> choices;

    private Long answer;


    private Integer duration;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<ChoiceRequest> getChoices() {
        return choices;
    }

    public void setChoices(List<ChoiceRequest> choices) {
        this.choices = choices;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public long getAnswer() {
        return answer;
    }

    public void setAnswer(long answer) {
        this.answer = answer;
    }
}