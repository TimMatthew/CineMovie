package org.ukma.spring.cinemovie.dto.favourites;

import lombok.Builder;

import java.util.UUID;

@Builder
public record FavResponseDto(
    UUID favId,
    UUID titleId,
    UUID userId
) {

}
