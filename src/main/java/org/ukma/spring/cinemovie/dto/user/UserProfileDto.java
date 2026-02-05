package org.ukma.spring.cinemovie.dto.user;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserProfileDto(
    UUID userId,
    String userName,
    String email,
    String login,
    boolean state
) {
}
