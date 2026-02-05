package org.ukma.spring.cinemovie.dto.user;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserTestDto(
    UUID id,
    String email,
    String password,
    String login,
    String name,
    boolean state
) {

}