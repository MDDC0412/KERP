package com.kerp.common.security;

import com.kerp.common.exception.ForbiddenException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final LocalSessionResolver localSessionResolver;

    public AuthInterceptor(LocalSessionResolver localSessionResolver) {
        this.localSessionResolver = localSessionResolver;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        CurrentUser currentUser = localSessionResolver.resolve(request.getHeader("Authorization"));
        AuthContextHolder.set(currentUser);

        RequireRole requireRole = findRoleRequirement(handlerMethod);
        if (requireRole != null && !currentUser.hasRole(requireRole.value())) {
            throw new ForbiddenException("Current account does not have the required role");
        }

        RequirePermission requirePermission = findPermissionRequirement(handlerMethod);
        if (requirePermission != null && !currentUser.hasPermission(requirePermission.value()) && !currentUser.hasRole("ADMIN")) {
            throw new ForbiddenException("Current account does not have the required permission");
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContextHolder.clear();
    }

    private RequireRole findRoleRequirement(HandlerMethod handlerMethod) {
        RequireRole methodRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        return methodRole != null ? methodRole : handlerMethod.getBeanType().getAnnotation(RequireRole.class);
    }

    private RequirePermission findPermissionRequirement(HandlerMethod handlerMethod) {
        RequirePermission methodPermission = handlerMethod.getMethodAnnotation(RequirePermission.class);
        return methodPermission != null ? methodPermission : handlerMethod.getBeanType().getAnnotation(RequirePermission.class);
    }
}
