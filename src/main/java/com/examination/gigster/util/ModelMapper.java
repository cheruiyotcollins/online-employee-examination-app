package com.examination.gigster.util;

import com.examination.gigster.model.Question;
import com.examination.gigster.model.Submission;
import com.examination.gigster.payload.ChoiceResponse;
import com.examination.gigster.model.User;
import com.examination.gigster.payload.QuestionResponse;
import com.examination.gigster.payload.UserSummary;
import com.examination.gigster.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ModelMapper {

    @Autowired
    SubmissionRepository submissionRepository;

    public static QuestionResponse mapPollToPollResponse(Question question, Map<Long, Long> choiceSubmissionMap, User creator, Long userVote) {
        QuestionResponse questionResponse = new QuestionResponse();
        questionResponse.setId(question.getId());
        questionResponse.setQuestion(question.getQuestion());
        questionResponse.setCreationDateTime(question.getCreatedAt());
        questionResponse.setExpirationDateTime(question.getExpirationDateTime());

         questionResponse.setSuccessRate("100%");

        Instant now = Instant.now();
        questionResponse.setExpired(question.getExpirationDateTime().isBefore(now));

        List<ChoiceResponse> choiceResponses = question.getChoices().stream().map(choice -> {
            ChoiceResponse choiceResponse = new ChoiceResponse();


            choiceResponse.setId(choice.getId());
            choiceResponse.setText(choice.getText());


            if(choiceSubmissionMap.containsKey(choice.getId())) {
                choiceResponse.setVoteCount(choiceSubmissionMap.get(choice.getId()));
            } else {
                choiceResponse.setVoteCount(0);
            }
            return choiceResponse;
        }).collect(Collectors.toList());

        questionResponse.setChoices(choiceResponses);
        UserSummary creatorSummary = new UserSummary(creator.getId(), creator.getUsername(), creator.getName(), creator.getRoleId());
        questionResponse.setCreatedBy(creatorSummary);

        if(userVote != null) {
            questionResponse.setSelectedChoice(userVote);
        }

        long totalVotes = questionResponse.getChoices().stream().mapToLong(ChoiceResponse::getVoteCount).sum();

        questionResponse.setTotalSubmission(totalVotes);



        return questionResponse;
    }



    public static QuestionResponse mapQuestionToQuestionResponse(Question question, Map<Long, Long> choiceSubmissionMap, User creator, Long userVote) {
        QuestionResponse questionResponse = new QuestionResponse();
        questionResponse.setId(question.getId());
        questionResponse.setQuestion(question.getQuestion());
        questionResponse.setExpirationDateTime(question.getExpirationDateTime());
        Instant now = Instant.now();
        questionResponse.setExpired(question.getExpirationDateTime().isBefore(now));

        List<ChoiceResponse> choiceResponses = question.getChoices().stream().map(choice -> {
            ChoiceResponse choiceResponse = new ChoiceResponse();
            choiceResponse.setId(choice.getId());
            choiceResponse.setText(choice.getText());

            if(choiceSubmissionMap.containsKey(choice.getId())) {
                choiceResponse.setVoteCount(choiceSubmissionMap.get(choice.getId()));
            } else {
                choiceResponse.setVoteCount(0);
            }
            return choiceResponse;
        }).collect(Collectors.toList());

        questionResponse.setChoices(choiceResponses);


        return questionResponse;
    }





}
