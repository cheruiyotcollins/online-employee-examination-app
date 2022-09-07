package com.example.konvergence.controller;

import com.example.konvergence.model.*;
import com.example.konvergence.payload.*;
import com.example.konvergence.repository.QuestionRepository;
import com.example.konvergence.repository.UserRepository;
import com.example.konvergence.repository.SubmissionRepository;
import com.example.konvergence.security.CurrentUser;
import com.example.konvergence.security.UserPrincipal;
import com.example.konvergence.service.QuestionService;
import com.example.konvergence.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.validation.Valid;
import java.net.URI;

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

    @GetMapping
    public PagedResponse<QuestionResponse> getPolls(@CurrentUser UserPrincipal currentUser,
                                                    @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                    @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return questionService.getAllQuestions(currentUser, page, size);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createQuestion( @RequestBody QuestionRequest questionRequest) {
        System.out.println("++++++++++++++++++"+questionRequest.getQuestionLength().getMinutes());
        Question question = questionService.createQuestion(questionRequest);


        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{questionId}")
                .buildAndExpand(question.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Question Created Successfully"));
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

}
