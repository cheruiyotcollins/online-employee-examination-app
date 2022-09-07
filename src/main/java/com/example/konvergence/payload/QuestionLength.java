package com.example.konvergence.payload;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

public class QuestionLength {


    @NotNull
    @Max(100)
    private Integer minutes;

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }
}
