package com.yatraflow.user.controller;

import com.yatraflow.user.dto.response.CurrentUserResponse;
import com.yatraflow.user.entity.User;
import com.yatraflow.user.service.UserService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;


    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> getCurrentUser() {

        log.info("Fetching authenticated user's profile.");

        User user = userService.getCurrentUser();

        List<String> roles = user.getRoles()
                .stream()
                .map(role -> role.getName().name())
                .toList();

        CurrentUserResponse response = CurrentUserResponse.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .roles(roles)
                .build();

        return ResponseEntity.ok(response);
    }
}
