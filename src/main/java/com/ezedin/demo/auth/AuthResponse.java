package com.ezedin.demo.auth;

public record AuthResponse(Long userId, String username, String accessToken) {
}
