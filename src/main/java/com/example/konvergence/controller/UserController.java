package com.example.konvergence.controller;

import com.example.konvergence.exception.ResourceNotFoundException;
import com.example.konvergence.model.User;
import com.example.konvergence.payload.*;
import com.example.konvergence.repository.QuestionRepository;
import com.example.konvergence.repository.UserRepository;
import com.example.konvergence.repository.SubmissionRepository;
import com.example.konvergence.security.UserPrincipal;
import com.example.konvergence.service.QuestionService;
import com.example.konvergence.security.CurrentUser;
import com.example.konvergence.util.AppConstants;
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
