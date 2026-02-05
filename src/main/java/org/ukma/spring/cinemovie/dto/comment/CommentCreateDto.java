package org.ukma.spring.cinemovie.dto.comment;

import lombok.Builder;

@Builder
public record CommentCreateDto(
    String userId,
    String titleId,
    byte rating,
    String info
) {
}
