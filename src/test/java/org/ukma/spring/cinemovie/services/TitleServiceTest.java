package org.ukma.spring.cinemovie.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ukma.spring.cinemovie.dto.title.TitleResponseDto;
import org.ukma.spring.cinemovie.dto.title.TitleUpsertDto;
import org.ukma.spring.cinemovie.entities.Title;
import org.ukma.spring.cinemovie.repos.TitleRepo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TitleServiceTest {

    @Mock
    private TitleRepo titleRepo;

    @InjectMocks
    private TitleService titleService;

    private UUID titleId;
    private TitleUpsertDto upsertDto;
    private Title existingTitle;

    @BeforeEach
    void setUp() {
        titleId = UUID.randomUUID();

        upsertDto = TitleUpsertDto.builder()
            .titleName("Inception")
            .overview("Dreams inside dreams")
            .keywords(List.of("dream", "mind", "heist"))
            .genres(List.of("sci-fi, thriller"))
            .actors(List.of("DiCaprio, Page"))
            .director(List.of("Nolan"))
            .releaseYear((short) 2010)
            .rating((byte) 8)
            .imageUrl("https://img")
            .build();

        existingTitle = Title.builder()
            .titleId(titleId)
            .tmdbId("12345")
            .titleName("Old name")
            .overview("Old overview")
            .keywords(List.of("old"))
            .genres(List.of("old"))
            .actors(List.of("old"))
            .director(List.of("old"))
            .releaseYear((short) 2000)
            .rating((byte) 5)
            .imageUrl("old-url")
            .build();
    }

    @Test
    void create_shouldSaveAndReturnId() {
        when(titleRepo.existsByTmdbId(anyString())).thenReturn(false);

        UUID savedId = UUID.randomUUID();

        when(titleRepo.saveAndFlush(any(Title.class))).thenAnswer(inv -> {
            Title arg = inv.getArgument(0);
            arg.setTitleId(savedId);
            return arg;
        });

        UUID res = titleService.create(upsertDto);

        assertEquals(savedId, res);

        ArgumentCaptor<Title> captor = ArgumentCaptor.forClass(Title.class);
        verify(titleRepo).saveAndFlush(captor.capture());

        Title saved = captor.getValue();
        assertNotNull(saved.getTmdbId());
        assertFalse(saved.getTmdbId().isBlank());

        assertEquals(upsertDto.titleName(), saved.getTitleName());
        assertEquals(upsertDto.overview(), saved.getOverview());
        assertEquals(upsertDto.keywords(), saved.getKeywords());
        assertEquals(upsertDto.genres(), saved.getGenres());
        assertEquals(upsertDto.actors(), saved.getActors());
        assertEquals(upsertDto.director(), saved.getDirector());
        assertEquals(upsertDto.releaseYear(), saved.getReleaseYear());
        assertEquals(upsertDto.rating(), saved.getRating());
        assertEquals(upsertDto.imageUrl(), saved.getImageUrl());

        verify(titleRepo, atLeastOnce()).existsByTmdbId(anyString());
    }

    @Test
    void create_shouldRetryTmdbId_whenAlreadyExists() {
        when(titleRepo.existsByTmdbId(anyString())).thenReturn(true, false);

        when(titleRepo.saveAndFlush(any(Title.class))).thenAnswer(inv -> {
            Title arg = inv.getArgument(0);
            arg.setTitleId(UUID.randomUUID());
            return arg;
        });

        UUID res = titleService.create(upsertDto);

        assertNotNull(res);
        verify(titleRepo, atLeast(2)).existsByTmdbId(anyString());
        verify(titleRepo).saveAndFlush(any(Title.class));
    }

    @Test
    void get_shouldReturnDto() {
        when(titleRepo.findById(titleId)).thenReturn(Optional.of(existingTitle));

        TitleResponseDto dto = titleService.get(titleId);

        assertEquals(existingTitle.getTitleId(), dto.id());
        assertEquals(existingTitle.getTmdbId(), dto.tmdbId());
        assertEquals(existingTitle.getTitleName(), dto.titleName());
        assertEquals(existingTitle.getOverview(), dto.overview());
        assertEquals(existingTitle.getKeywords(), dto.keywords());
        assertEquals(existingTitle.getGenres(), dto.genres());
        assertEquals(existingTitle.getActors(), dto.actors());
        assertEquals(existingTitle.getDirector(), dto.director());
        assertEquals(existingTitle.getReleaseYear(), dto.releaseYear());
        assertEquals(existingTitle.getRating(), dto.rating());
        assertEquals(existingTitle.getImageUrl(), dto.imageUrl());
    }

    @Test
    void get_shouldThrow_whenNotFound() {
        when(titleRepo.findById(titleId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> titleService.get(titleId)
        );

        assertTrue(ex.getMessage().contains("Title with id " + titleId + " is not found"));
    }

    @Test
    void getAll_shouldReturnList() {
        Title t2 = Title.builder()
            .titleId(UUID.randomUUID())
            .tmdbId("999")
            .titleName("Another")
            .overview("O2")
            .keywords(List.of("k2"))
            .genres(List.of("g2"))
            .actors(List.of("a2"))
            .director(List.of("d2"))
            .releaseYear((short) 2022)
            .rating((byte) 7)
            .imageUrl("img2")
            .build();

        when(titleRepo.findAll()).thenReturn(List.of(existingTitle, t2));

        List<TitleResponseDto> list = titleService.getAll();

        assertEquals(2, list.size());
        assertEquals(existingTitle.getTitleId(), list.get(0).id());
        assertEquals(t2.getTitleId(), list.get(1).id());
    }

    @Test
    void update_shouldUpdateFieldsAndReturnDto() {
        when(titleRepo.findById(titleId)).thenReturn(Optional.of(existingTitle));
        when(titleRepo.saveAndFlush(any(Title.class))).thenAnswer(inv -> inv.getArgument(0));

        TitleUpsertDto res = titleService.update(titleId, upsertDto);

        assertEquals(upsertDto.titleName(), existingTitle.getTitleName());
        assertEquals(upsertDto.overview(), existingTitle.getOverview());
        assertEquals(upsertDto.keywords(), existingTitle.getKeywords());
        assertEquals(upsertDto.genres(), existingTitle.getGenres());
        assertEquals(upsertDto.actors(), existingTitle.getActors());
        assertEquals(upsertDto.director(), existingTitle.getDirector());
        assertEquals(upsertDto.releaseYear(), existingTitle.getReleaseYear());
        assertEquals(upsertDto.rating(), existingTitle.getRating());
        assertEquals(upsertDto.imageUrl(), existingTitle.getImageUrl());

        assertEquals(upsertDto.titleName(), res.titleName());
        assertEquals(upsertDto.overview(), res.overview());
        assertEquals(upsertDto.keywords(), res.keywords());
        assertEquals(upsertDto.genres(), res.genres());
        assertEquals(upsertDto.actors(), res.actors());
        assertEquals(upsertDto.director(), res.director());
        assertEquals(upsertDto.releaseYear(), res.releaseYear());
        assertEquals(upsertDto.rating(), res.rating());
        assertEquals(upsertDto.imageUrl(), res.imageUrl());

        verify(titleRepo).saveAndFlush(existingTitle);
    }

    @Test
    void update_shouldThrow_whenNotFound() {
        when(titleRepo.findById(titleId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> titleService.update(titleId, upsertDto)
        );

        assertTrue(ex.getMessage().contains("Title with id " + titleId + " is not found"));
        verify(titleRepo, never()).saveAndFlush(any());
    }

    @Test
    void delete_shouldDeleteAndReturnTrue() {
        when(titleRepo.findById(titleId)).thenReturn(Optional.of(existingTitle));

        boolean res = titleService.delete(titleId);

        assertTrue(res);
        verify(titleRepo).delete(existingTitle);
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(titleRepo.findById(titleId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> titleService.delete(titleId)
        );

        assertTrue(ex.getMessage().contains("Title with id " + titleId + " is not found"));
        verify(titleRepo, never()).delete(any());
    }
}
