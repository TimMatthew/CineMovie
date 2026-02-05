package org.ukma.spring.cinemovie.dto.user;

import lombok.Builder;

@Builder
public record UserAuthDto(
    String login,
    String password
) {

}
