package org.ukma.spring.cinemovie.dto.title;

import lombok.Builder;

import java.util.List;

@Builder
public record TitleUpsertDto(
    String titleName,
    String overview,
    List<String> keywords,
    List<String> genres,
    List<String> actors,
    List<String> director,
    short releaseYear,
    byte rating,
    String imageUrl
) {

}
