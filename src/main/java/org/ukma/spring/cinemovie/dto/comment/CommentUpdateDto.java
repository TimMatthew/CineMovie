package org.ukma.spring.cinemovie.dto.comment;

import lombok.Builder;
import org.ukma.spring.cinemovie.entities.User;

@Builder
public record CommentUpdateDto(
    User userId,
    byte rating,
    String info
) {
}
