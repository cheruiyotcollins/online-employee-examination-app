package com.examination.gigster.controller;

import com.examination.gigster.model.User;
import com.examination.gigster.payload.*;
import com.examination.gigster.repository.SubmissionRepository;
import com.examination.gigster.repository.UserRepository;
import com.examination.gigster.util.AppConstants;
import com.examination.gigster.exception.ResourceNotFoundException;
import com.examination.gigster.repository.QuestionRepository;
import com.examination.gigster.security.UserPrincipal;
import com.examination.gigster.service.QuestionService;
import com.examination.gigster.security.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private QuestionService questionService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        User user= userRepository.findById(currentUser.getId()).get();
       System.out.println(":::::::::::::kipkirui:::::::::::::"+user.getRoles().toArray()[0]);

        UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName(),user.getRoleId());
        return userSummary;
    }

    @GetMapping("/user/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username) {
        Boolean isAvailable = !userRepository.existsByUsername(username);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/user/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/users/{username}")
    public UserProfile getUserProfile(@PathVariable(value = "username") String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        long pollCount = questionRepository.countByCreatedBy(user.getId());
        long voteCount = submissionRepository.countByUserId(user.getId());


        UserProfile userProfile = new UserProfile(user.getId(), user.getUsername(), user.getName(), user.getCreatedAt(), pollCount, voteCount, user.getRoles());
       return userProfile;
    }

    @GetMapping("/users/{username}/questions")
    public PagedResponse<QuestionResponse> getPollsCreatedBy(@PathVariable(value = "username") String username,
                                                             @CurrentUser UserPrincipal currentUser,
                                                             @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                             @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return questionService.getQuestionsCreatedBy(username, currentUser, page, size);
    }

    @GetMapping("/users/{username}/submissions")
    public PagedResponse<QuestionResponse> getPollsVotedBy(@PathVariable(value = "username") String username,
                                                           @CurrentUser UserPrincipal currentUser,
                                                           @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                           @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return questionService.getQuestionsSubmittedBy(username, currentUser, page, size);
    }

}
