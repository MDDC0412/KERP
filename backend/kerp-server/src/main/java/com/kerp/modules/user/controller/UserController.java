package com.kerp.modules.user.controller;

import com.kerp.common.api.ApiResponse;
import com.kerp.modules.user.dto.UserCreateRequest;
import com.kerp.modules.user.dto.UserListItem;
import com.kerp.modules.user.entity.User;
import com.kerp.modules.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<List<UserListItem>> listUsers() {
        return ApiResponse.success(userService.listUsers());
    }

    @PostMapping
    public ApiResponse<User> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.success(userService.createUser(request));
    }
}

