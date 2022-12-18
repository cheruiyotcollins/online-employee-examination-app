package com.examination.gigster.repository;

import com.examination.gigster.model.ChoiceQuestionCount;
import com.examination.gigster.model.Question;
import com.examination.gigster.model.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    Optional<Question> findById(Long pollId);

    Page<Question> findByCreatedBy(Long userId, Pageable pageable);

    long countByCreatedBy(Long userId);

    List<Question> findByIdIn(List<Long> pollIds);

    List<Question> findByIdIn(List<Long> pollIds, Sort sort);

//    @Query("SELECT v.id,v.question,v.choices FROM Question v ")

//    @Query(
//            value = "SELECT id, question,created_at,updated_at,created_by, updated_by,expiration_date_time FROM questions",
//            nativeQuery = true)
//    List<Question> findByUserIdAndPollId();
}
