package org.ukma.spring.cinemovie.dto.user;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponseDto(
    UUID userId,
    String userName,
    String login,
    boolean state
) {

}