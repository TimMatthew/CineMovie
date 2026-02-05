package org.ukma.spring.cinemovie.dto.comment;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record CommentResponseDto(
    UUID id,
    UUID userId,
    UUID titleId,
    OffsetDateTime creationDate,
    byte rating,
    String info
) {
}
