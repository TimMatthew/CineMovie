package org.ukma.spring.cinemovie.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ukma.spring.cinemovie.dto.favourites.FavCreateDto;
import org.ukma.spring.cinemovie.dto.favourites.FavResponseDto;
import org.ukma.spring.cinemovie.entities.FavouriteTitle;
import org.ukma.spring.cinemovie.entities.Title;
import org.ukma.spring.cinemovie.entities.User;
import org.ukma.spring.cinemovie.repos.FavouriteRepo;
import org.ukma.spring.cinemovie.repos.TitleRepo;
import org.ukma.spring.cinemovie.repos.UserRepo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavouriteServiceTest {

    @Mock private UserRepo userRepo;
    @Mock private TitleRepo titleRepo;
    @Mock private FavouriteRepo favouriteRepo;

    @InjectMocks
    private FavouriteService favouriteService;

    private UUID userId;
    private UUID titleId;
    private UUID favId;

    private User user;
    private Title title;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        titleId = UUID.randomUUID();
        favId = UUID.randomUUID();

        user = mock(User.class);
        title = mock(Title.class);
    }

    @Test
    void create_shouldSaveAndReturnId() {
        FavCreateDto dto = new FavCreateDto(userId, titleId);

        when(userRepo.findById(dto.userId())).thenReturn(Optional.of(user));
        when(titleRepo.findById(dto.titleId())).thenReturn(Optional.of(title));

        FavouriteTitle saved = FavouriteTitle.builder()
            .user(user)
            .title(title)
            .build();
        saved.setFavId(favId);

        when(favouriteRepo.saveAndFlush(any(FavouriteTitle.class))).thenReturn(saved);

        UUID result = favouriteService.create(dto);

        assertEquals(favId, result);

        ArgumentCaptor<FavouriteTitle> captor = ArgumentCaptor.forClass(FavouriteTitle.class);
        verify(favouriteRepo).saveAndFlush(captor.capture());

        FavouriteTitle passed = captor.getValue();
        assertSame(user, passed.getUser());
        assertSame(title, passed.getTitle());

        verify(userRepo).findById(dto.userId());
        verify(titleRepo).findById(dto.titleId());
        verifyNoMoreInteractions(userRepo, titleRepo, favouriteRepo);
    }

    @Test
    void create_shouldThrow_whenUserNotFound() {
        FavCreateDto dto = new FavCreateDto(titleId, userId);

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> favouriteService.create(dto));

        verify(userRepo).findById(userId);
        verifyNoInteractions(titleRepo);
        verify(favouriteRepo, never()).saveAndFlush(any());
    }

    @Test
    void create_shouldThrow_whenTitleNotFound() {
        FavCreateDto dto = new FavCreateDto(titleId, userId);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(titleRepo.findById(titleId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> favouriteService.create(dto));

        verify(userRepo).findById(userId);
        verify(titleRepo).findById(titleId);
        verify(favouriteRepo, never()).saveAndFlush(any());
    }

    @Test
    void getAll_shouldReturnListOfDtos() {
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        when(user.getUserId()).thenReturn(userId);

        UUID favId1 = UUID.randomUUID();
        UUID favId2 = UUID.randomUUID();

        Title title1 = mock(Title.class);
        Title title2 = mock(Title.class);

        UUID titleId1 = UUID.randomUUID();
        UUID titleId2 = UUID.randomUUID();
        when(title1.getTitleId()).thenReturn(titleId1);
        when(title2.getTitleId()).thenReturn(titleId2);

        FavouriteTitle ft1 = mock(FavouriteTitle.class);
        when(ft1.getFavId()).thenReturn(favId1);
        when(ft1.getUser()).thenReturn(user);
        when(ft1.getTitle()).thenReturn(title1);

        FavouriteTitle ft2 = mock(FavouriteTitle.class);
        when(ft2.getFavId()).thenReturn(favId2);
        when(ft2.getUser()).thenReturn(user);
        when(ft2.getTitle()).thenReturn(title2);

        when(favouriteRepo.findAllByUser(user)).thenReturn(List.of(ft1, ft2));

        List<FavResponseDto> res = favouriteService.getAll(userId);

        assertEquals(2, res.size());

        assertEquals(favId1, res.getFirst().favId());
        assertEquals(userId,  res.get(0).userId());
        assertEquals(titleId1, res.get(0).titleId());

        assertEquals(favId2, res.get(1).favId());
        assertEquals(userId,  res.get(1).userId());
        assertEquals(titleId2, res.get(1).titleId());

        verify(userRepo).findById(userId);
        verify(favouriteRepo).findAllByUser(user);
        verifyNoMoreInteractions(userRepo, favouriteRepo);
        verifyNoInteractions(titleRepo);
    }

    @Test
    void getAll_shouldThrow_whenUserNotFound() {
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> favouriteService.getAll(userId));

        verify(userRepo).findById(userId);
        verifyNoInteractions(favouriteRepo, titleRepo);
    }

    @Test
    void delete_shouldDeleteAndReturnTrue() {
        when(favouriteRepo.existsById(favId)).thenReturn(true);

        boolean res = favouriteService.delete(favId);

        assertTrue(res);
        verify(favouriteRepo).existsById(favId);
        verify(favouriteRepo).deleteById(favId);
        verifyNoMoreInteractions(favouriteRepo);
        verifyNoInteractions(userRepo, titleRepo);
    }

    @Test
    void delete_shouldThrow_whenFavouriteNotFound() {
        when(favouriteRepo.existsById(favId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> favouriteService.delete(favId));

        verify(favouriteRepo).existsById(favId);
        verify(favouriteRepo, never()).deleteById(any());
        verifyNoInteractions(userRepo, titleRepo);
    }
}
