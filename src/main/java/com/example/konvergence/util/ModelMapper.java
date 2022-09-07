package com.example.konvergence.util;

import com.example.konvergence.model.Question;
import com.example.konvergence.model.User;
import com.example.konvergence.payload.ChoiceResponse;
import com.example.konvergence.payload.QuestionResponse;
import com.example.konvergence.payload.UserSummary;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelMapper {

    public static QuestionResponse mapPollToPollResponse(Question question, Map<Long, Long> choiceSubmissionMap, User creator, Long userVote) {
        QuestionResponse questionResponse = new QuestionResponse();
        questionResponse.setId(question.getId());
        questionResponse.setQuestion(question.getQuestion());
        questionResponse.setCreationDateTime(question.getCreatedAt());
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
        UserSummary creatorSummary = new UserSummary(creator.getId(), creator.getUsername(), creator.getName(), creator.getRoleId());
        questionResponse.setCreatedBy(creatorSummary);

        if(userVote != null) {
            questionResponse.setSelectedChoice(userVote);
        }

        long totalVotes = questionResponse.getChoices().stream().mapToLong(ChoiceResponse::getVoteCount).sum();
        questionResponse.setTotalVotes(totalVotes);

        return questionResponse;
    }

}
