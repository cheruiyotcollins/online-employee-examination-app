package com.examination.gigster.controller;

import com.examination.gigster.model.Answer;
import com.examination.gigster.model.Question;
import com.examination.gigster.payload.*;
import com.examination.gigster.repository.SubmissionRepository;
import com.examination.gigster.repository.UserRepository;
import com.examination.gigster.util.AppConstants;
import com.examination.gigster.repository.QuestionRepository;
import com.examination.gigster.security.CurrentUser;
import com.examination.gigster.security.UserPrincipal;
import com.examination.gigster.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionService questionService;

    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @GetMapping("/reports")
    public PagedResponse<QuestionResponse> getReports(@CurrentUser UserPrincipal currentUser,
                                                    @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                    @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return questionService.getAllReportsPerQuestion(currentUser, page, size);
    }
    @GetMapping("/list")
    public PagedResponse<QuestionResponse> getQuestions(@CurrentUser UserPrincipal currentUser,
                                                    @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                    @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return questionService.getAllQuestions(currentUser, page, size);
    }



    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ApiResponse createQuestion( @RequestBody QuestionRequest questionRequest) {
        return questionService.createQuestion(questionRequest);





    }



    @GetMapping("/{questionId}")
    public QuestionResponse getQuestionById(@CurrentUser UserPrincipal currentUser,
                                        @PathVariable Long questionId) {
        return questionService.getQuestionById(questionId, currentUser);
    }



    @PostMapping("/{questionId}/submission")
    @PreAuthorize("hasRole('USER')")
    public QuestionResponse submitAnswer(@CurrentUser UserPrincipal currentUser,
                                     @PathVariable Long questionId,
                                     @Valid @RequestBody SubmissionRequest submissionRequest) {
        return questionService.submitAnswerAndGetUpdatedSubmission(questionId, submissionRequest, currentUser);
    }

    @PostMapping("/submission")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?>  coolsubmitAnswer(@CurrentUser UserPrincipal currentUser,@RequestBody List<SubmissionRequest> submissionRequest){

        return questionService.coolSubmitAnswerAndGetUpdatedSubmission(submissionRequest, currentUser);
    }
    @GetMapping("/score")
    @PreAuthorize("hasRole('USER')")
    public ScoreResponse viewScore(@CurrentUser UserPrincipal currentUser){

        return questionService.viewScores(currentUser);
    }

    }





