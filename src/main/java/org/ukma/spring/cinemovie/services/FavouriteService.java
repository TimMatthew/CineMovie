package org.ukma.spring.cinemovie.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ukma.spring.cinemovie.dto.favourites.FavCreateDto;
import org.ukma.spring.cinemovie.dto.favourites.FavResponseDto;
import org.ukma.spring.cinemovie.entities.FavouriteTitle;
import org.ukma.spring.cinemovie.entities.Title;
import org.ukma.spring.cinemovie.entities.User;
import org.ukma.spring.cinemovie.repos.FavouriteRepo;
import org.ukma.spring.cinemovie.repos.TitleRepo;
import org.ukma.spring.cinemovie.repos.UserRepo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavouriteService {

    private final UserRepo userRepo;
    private final TitleRepo titleRepo;
    private final FavouriteRepo favouriteRepo;

    public UUID create(FavCreateDto dto) {
        User user = userRepo.findById(dto.userId())
            .orElseThrow(() -> new IllegalArgumentException("User with id " + dto.userId() + " is not found"));

        Title title = titleRepo.findById(dto.titleId())
            .orElseThrow(() -> new IllegalArgumentException("Title with id " + dto.titleId() + " is not found"));

        FavouriteTitle fav = FavouriteTitle.builder()
            .user(user)
            .title(title)
            .build();

        fav = favouriteRepo.saveAndFlush(fav);
        return fav.getFavId();
    }

    public List<FavResponseDto> getAll(UUID userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " is not found"));

        return favouriteRepo.findAllByUser(user).stream()
            .map(this::toDto)
            .toList();
    }

    public boolean delete(UUID favId) {
        if (!favouriteRepo.existsById(favId)) {
            throw new IllegalArgumentException("Favourite with id " + favId + " is not found");
        }
        favouriteRepo.deleteById(favId);
        return true;
    }

    private FavResponseDto toDto(FavouriteTitle ft) {
        return FavResponseDto.builder()
            .favId(ft.getFavId())
            .titleId(ft.getTitle().getTitleId())
            .userId(ft.getUser().getUserId())
            .build();
    }
}
