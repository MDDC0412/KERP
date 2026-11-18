package com.kerp.modules.auth.service;

import com.kerp.modules.auth.dto.LoginRequest;
import com.kerp.modules.auth.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
