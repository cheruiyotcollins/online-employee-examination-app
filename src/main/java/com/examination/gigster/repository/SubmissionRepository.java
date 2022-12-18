package com.examination.gigster.repository;

import com.examination.gigster.model.ChoiceQuestionCount;
import com.examination.gigster.model.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    @Query("SELECT NEW com.examination.gigster.model.ChoiceQuestionCount(v.choice.id, count(v.id)) FROM Submission v WHERE v.question.id in :questionIds GROUP BY v.choice.id")
    List<ChoiceQuestionCount> countByPollIdInGroupByChoiceId(@Param("questionIds") List<Long> questionIds);

    @Query("SELECT NEW com.examination.gigster.model.ChoiceQuestionCount(v.choice.id, count(v.id)) FROM Submission v WHERE v.question.id = :questionId GROUP BY v.choice.id")
    List<ChoiceQuestionCount> countByPollIdGroupByChoiceId(@Param("questionId") Long questionId);

    @Query("SELECT v FROM Submission v where v.user.id = :userId and v.question.id in :questionIds")
    List<Submission> findByUserIdAndPollIdIn(@Param("userId") Long userId, @Param("questionIds") List<Long> questionIds);

    @Query("SELECT v FROM Submission v where v.user.id = :userId and v.question.id = :questionId")
    Submission findByUserIdAndPollId(@Param("userId") Long userId, @Param("questionId") Long questionId);

    @Query("SELECT COUNT(v.id) from Submission v where v.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT v.question.id FROM Submission v WHERE v.user.id = :userId")
    Page<Long> findVotedPollIdsByUserId(@Param("userId") Long userId, Pageable pageable);
}

