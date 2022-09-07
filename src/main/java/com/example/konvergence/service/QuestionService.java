package com.example.konvergence.service;

import com.example.konvergence.exception.BadRequestException;
import com.example.konvergence.exception.ResourceNotFoundException;
import com.example.konvergence.model.*;
import com.example.konvergence.payload.PagedResponse;
import com.example.konvergence.payload.QuestionRequest;
import com.example.konvergence.payload.QuestionResponse;
import com.example.konvergence.payload.SubmissionRequest;
import com.example.konvergence.repository.QuestionRepository;
import com.example.konvergence.repository.UserRepository;
import com.example.konvergence.repository.SubmissionRepository;
import com.example.konvergence.security.UserPrincipal;
import com.example.konvergence.util.AppConstants;
import com.example.konvergence.util.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);

    public PagedResponse<QuestionResponse> getAllQuestions(UserPrincipal currentUser, int page, int size) {
        validatePageNumberAndSize(page, size);

        // Retrieve Polls
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Question> polls = questionRepository.findAll(pageable);

        if(polls.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), polls.getNumber(),
                    polls.getSize(), polls.getTotalElements(), polls.getTotalPages(), polls.isLast());
        }

        // Map Polls to PollResponses containing vote counts and poll creator details
        List<Long> questionids = polls.map(Question::getId).getContent();
        Map<Long, Long> choiceVoteCountMap = getChoiceVoteCountMap(questionids);
        Map<Long, Long> pollUserVoteMap = getQuestionUserSubmissioniMap(currentUser, questionids);
        Map<Long, User> creatorMap = getQuestionCreatorMap(polls.getContent());

        List<QuestionResponse> questionRespons = polls.map(poll -> {
            return ModelMapper.mapPollToPollResponse(poll,
                    choiceVoteCountMap,
                    creatorMap.get(poll.getCreatedBy()),
                    pollUserVoteMap == null ? null : pollUserVoteMap.getOrDefault(poll.getId(), null));
        }).getContent();

        return new PagedResponse<>(questionRespons, polls.getNumber(),
                polls.getSize(), polls.getTotalElements(), polls.getTotalPages(), polls.isLast());
    }

    public PagedResponse<QuestionResponse> getQuestionsCreatedBy(String username, UserPrincipal currentUser, int page, int size) {
        validatePageNumberAndSize(page, size);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Retrieve all polls created by the given username
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Question> polls = questionRepository.findByCreatedBy(user.getId(), pageable);

        if (polls.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), polls.getNumber(),
                    polls.getSize(), polls.getTotalElements(), polls.getTotalPages(), polls.isLast());
        }

        // Map Polls to PollResponses containing vote counts and poll creator details
        List<Long> questionids = polls.map(Question::getId).getContent();
        Map<Long, Long> choiceVoteCountMap = getChoiceVoteCountMap(questionids);
        Map<Long, Long> pollUserVoteMap = getQuestionUserSubmissioniMap(currentUser, questionids);

        List<QuestionResponse> questionRespons = polls.map(poll -> {
            return ModelMapper.mapPollToPollResponse(poll,
                    choiceVoteCountMap,
                    user,
                    pollUserVoteMap == null ? null : pollUserVoteMap.getOrDefault(poll.getId(), null));
        }).getContent();

        return new PagedResponse<>(questionRespons, polls.getNumber(),
                polls.getSize(), polls.getTotalElements(), polls.getTotalPages(), polls.isLast());
    }

    public PagedResponse<QuestionResponse> getQuestionsSubmittedBy(String username, UserPrincipal currentUser, int page, int size) {
        validatePageNumberAndSize(page, size);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Retrieve all pollIds in which the given username has voted
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Long> userVotedPollIds = submissionRepository.findVotedPollIdsByUserId(user.getId(), pageable);

        if (userVotedPollIds.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), userVotedPollIds.getNumber(),
                    userVotedPollIds.getSize(), userVotedPollIds.getTotalElements(),
                    userVotedPollIds.getTotalPages(), userVotedPollIds.isLast());
        }

        // Retrieve all poll details from the voted pollIds.
        List<Long> questionids = userVotedPollIds.getContent();

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Question> questions = questionRepository.findByIdIn(questionids, sort);

        // Map Polls to PollResponses containing vote counts and poll creator details
        Map<Long, Long> choiceVoteCountMap = getChoiceVoteCountMap(questionids);
        Map<Long, Long> pollUserVoteMap = getQuestionUserSubmissioniMap(currentUser, questionids);
        Map<Long, User> creatorMap = getQuestionCreatorMap(questions);

        List<QuestionResponse> questionRespons = questions.stream().map(poll -> {
            return ModelMapper.mapPollToPollResponse(poll,
                    choiceVoteCountMap,
                    creatorMap.get(poll.getCreatedBy()),
                    pollUserVoteMap == null ? null : pollUserVoteMap.getOrDefault(poll.getId(), null));
        }).collect(Collectors.toList());

        return new PagedResponse<>(questionRespons, userVotedPollIds.getNumber(), userVotedPollIds.getSize(), userVotedPollIds.getTotalElements(), userVotedPollIds.getTotalPages(), userVotedPollIds.isLast());
    }


    public Question createQuestion(QuestionRequest questionRequest) {
        Question question = new Question();
        question.setQuestion(questionRequest.getQuestion());

        questionRequest.getChoices().forEach(choiceRequest -> {
            question.addChoice(new Choice(choiceRequest.getText()));
        });

        Instant now = Instant.now();
        Instant expirationDateTime = now.plus(Duration.ofMinutes(questionRequest.getQuestionLength().getMinutes()));

        question.setExpirationDateTime(expirationDateTime);

        return questionRepository.save(question);
    }

    public QuestionResponse getQuestionById(Long pollId, UserPrincipal currentUser) {
        Question question = questionRepository.findById(pollId).orElseThrow(
                () -> new ResourceNotFoundException("Question", "id", pollId));

        // Retrieve Submission Counts of every choice belonging to the current question
        List<ChoiceQuestionCount> votes = submissionRepository.countByPollIdGroupByChoiceId(pollId);

        Map<Long, Long> choiceVotesMap = votes.stream()
                .collect(Collectors.toMap(ChoiceQuestionCount::getChoiceId, ChoiceQuestionCount::getSubmissionCount));

        // Retrieve question creator details
        User creator = userRepository.findById(question.getCreatedBy())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", question.getCreatedBy()));

        // Retrieve vote done by logged in user
        Submission userSubmission = null;
        if(currentUser != null) {
            userSubmission = submissionRepository.findByUserIdAndPollId(currentUser.getId(), pollId);
        }

        return ModelMapper.mapPollToPollResponse(question, choiceVotesMap,
                creator, userSubmission != null ? userSubmission.getChoice().getId(): null);
    }

    public QuestionResponse submitAnswerAndGetUpdatedSubmission(Long pollId, SubmissionRequest submissionRequest, UserPrincipal currentUser) {
        Question question = questionRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", pollId));

        if(question.getExpirationDateTime().isBefore(Instant.now())) {
            throw new BadRequestException("Sorry! This Question has already expired");
        }

        User user = userRepository.getOne(currentUser.getId());

        Choice selectedChoice = question.getChoices().stream()
                .filter(choice -> choice.getId().equals(submissionRequest.getChoiceId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Choice", "id", submissionRequest.getChoiceId()));

        Submission submission = new Submission();
        submission.setQuestion(question);
        submission.setUser(user);
        submission.setChoice(selectedChoice);

        try {
            submission = submissionRepository.save(submission);
        } catch (DataIntegrityViolationException ex) {
            logger.info("User {} has already voted in Question {}", currentUser.getId(), pollId);
            throw new BadRequestException("Sorry! You have already cast your submission in this question");
        }

        //-- Submission Saved, Return the updated Question Response now --

        // Retrieve Submission Counts of every choice belonging to the current question
        List<ChoiceQuestionCount> votes = submissionRepository.countByPollIdGroupByChoiceId(pollId);

        Map<Long, Long> choiceVotesMap = votes.stream()
                .collect(Collectors.toMap(ChoiceQuestionCount::getChoiceId, ChoiceQuestionCount::getSubmissionCount));

        // Retrieve question creator details
        User creator = userRepository.findById(question.getCreatedBy())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", question.getCreatedBy()));

        return ModelMapper.mapPollToPollResponse(question, choiceVotesMap, creator, submission.getChoice().getId());
    }


    private void validatePageNumberAndSize(int page, int size) {
        if(page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if(size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }

    private Map<Long, Long> getChoiceVoteCountMap(List<Long> pollIds) {
        // Retrieve Submission Counts of every Choice belonging to the given pollIds
        List<ChoiceQuestionCount> votes = submissionRepository.countByPollIdInGroupByChoiceId(pollIds);

        Map<Long, Long> choiceVotesMap = votes.stream()
                .collect(Collectors.toMap(ChoiceQuestionCount::getChoiceId, ChoiceQuestionCount::getSubmissionCount));

        return choiceVotesMap;
    }

    private Map<Long, Long> getQuestionUserSubmissioniMap(UserPrincipal currentUser, List<Long> pollIds) {
        // Retrieve Votes done by the logged in user to the given pollIds
        Map<Long, Long> pollUserVoteMap = null;
        if(currentUser != null) {
            List<Submission> userSubmissions = submissionRepository.findByUserIdAndPollIdIn(currentUser.getId(), pollIds);

            pollUserVoteMap = userSubmissions.stream()
                    .collect(Collectors.toMap(submission -> submission.getQuestion().getId(), submission -> submission.getChoice().getId()));
        }
        return pollUserVoteMap;
    }

    Map<Long, User> getQuestionCreatorMap(List<Question> questions) {
        // Get Question Creator details of the given list of questions
        List<Long> creatorIds = questions.stream()
                .map(Question::getCreatedBy)
                .distinct()
                .collect(Collectors.toList());

        List<User> creators = userRepository.findByIdIn(creatorIds);
        Map<Long, User> creatorMap = creators.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return creatorMap;
    }
}
