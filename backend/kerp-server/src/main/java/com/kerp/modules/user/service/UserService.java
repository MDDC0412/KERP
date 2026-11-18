package com.kerp.modules.user.service;

import com.kerp.modules.user.dto.UserCreateRequest;
import com.kerp.modules.user.dto.UserListItem;
import com.kerp.modules.user.entity.User;

import java.util.List;

public interface UserService {

    User createUser(UserCreateRequest request);

    List<UserListItem> listUsers();
}

