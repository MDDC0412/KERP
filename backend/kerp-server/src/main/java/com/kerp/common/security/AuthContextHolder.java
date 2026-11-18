package com.kerp.common.security;

import com.kerp.common.exception.UnauthorizedException;

public final class AuthContextHolder {

    private static final ThreadLocal<CurrentUser> CONTEXT = new ThreadLocal<>();

    private AuthContextHolder() {
    }

    public static void set(CurrentUser currentUser) {
        CONTEXT.set(currentUser);
    }

    public static CurrentUser get() {
        return CONTEXT.get();
    }

    public static CurrentUser getRequired() {
        CurrentUser currentUser = CONTEXT.get();
        if (currentUser == null) {
            throw new UnauthorizedException("Current request is not authenticated");
        }
        return currentUser;
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
