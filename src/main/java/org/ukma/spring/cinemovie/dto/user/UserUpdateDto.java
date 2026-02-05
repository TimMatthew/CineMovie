package org.ukma.spring.cinemovie.dto.user;

import lombok.Builder;

@Builder
public record UserUpdateDto(
    String login,
    String password,
    String userName
) {

}