package org.ukma.spring.cinemovie.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ukma.spring.cinemovie.dto.title.TitleResponseDto;
import org.ukma.spring.cinemovie.dto.title.TitleUpsertDto;
import org.ukma.spring.cinemovie.entities.Title;
import org.ukma.spring.cinemovie.repos.TitleRepo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TitleService {

    private final TitleRepo titleRepo;

    public UUID create(TitleUpsertDto dto) {
        String tmdb = generateUniqueTmdbId();

        Title entity = Title.builder()
            .tmdbId(tmdb)
            .titleName(dto.titleName())
            .overview(dto.overview())
            .keywords(dto.keywords())
            .genres(dto.genres())
            .actors(dto.actors())
            .director(dto.director())
            .releaseYear(dto.releaseYear())
            .rating(dto.rating())
            .imageUrl(dto.imageUrl())
            .build();

        entity = titleRepo.saveAndFlush(entity);
        return entity.getTitleId();
    }

    public TitleResponseDto get(UUID id) {
        Title title = titleRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Title with id " + id + " is not found"));

        return toResponseDto(title);
    }

    public List<TitleResponseDto> getAll() {
        return titleRepo.findAll().stream()
            .map(this::toResponseDto)
            .toList();
    }

    public TitleUpsertDto update(UUID id, TitleUpsertDto dto) {
        Title title = titleRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Title with id " + id + " is not found"));

        title.setTitleName(dto.titleName());
        title.setOverview(dto.overview());
        title.setKeywords(dto.keywords());
        title.setGenres(dto.genres());
        title.setActors(dto.actors());
        title.setDirector(dto.director());
        title.setReleaseYear(dto.releaseYear());
        title.setRating(dto.rating());
        title.setImageUrl(dto.imageUrl());

        titleRepo.saveAndFlush(title);
        return toUpsertDto(title);
    }

    public boolean delete(UUID id) {
        Title title = titleRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Title with id " + id + " is not found"));

        titleRepo.delete(title);
        return true;
    }

    private TitleUpsertDto toUpsertDto(Title t) {
        return TitleUpsertDto.builder()
            .titleName(t.getTitleName())
            .overview(t.getOverview())
            .keywords(t.getKeywords())
            .genres(t.getGenres())
            .actors(t.getActors())
            .director(t.getDirector())
            .releaseYear(t.getReleaseYear())
            .rating(t.getRating())
            .imageUrl(t.getImageUrl())
            .build();
    }

    private TitleResponseDto toResponseDto(Title t) {
        return TitleResponseDto.builder()
            .id(t.getTitleId())
            .tmdbId(t.getTmdbId())
            .titleName(t.getTitleName())
            .overview(t.getOverview())
            .keywords(t.getKeywords())
            .genres(t.getGenres())
            .actors(t.getActors())
            .director(t.getDirector())
            .releaseYear(t.getReleaseYear())
            .rating(t.getRating())
            .imageUrl(t.getImageUrl())
            .build();
    }

    private String generateUniqueTmdbId() {
        String tmdbId;
        do {
            long randomNumber = (long) (Math.random() * 1_000_000L);
            tmdbId = String.valueOf(randomNumber);
        } while (titleRepo.existsByTmdbId(tmdbId));
        return tmdbId;
    }
}
