package com.examination.gigster.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.examination.gigster.model.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

}
