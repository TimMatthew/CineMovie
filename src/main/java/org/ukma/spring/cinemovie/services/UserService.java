package org.ukma.spring.cinemovie.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ukma.spring.cinemovie.dto.user.UserProfileDto;
import org.ukma.spring.cinemovie.dto.user.UserRegisterDto;
import org.ukma.spring.cinemovie.dto.user.UserResponseDto;
import org.ukma.spring.cinemovie.dto.user.UserTestDto;
import org.ukma.spring.cinemovie.dto.user.UserUpdateDto;
import org.ukma.spring.cinemovie.entities.User;
import org.ukma.spring.cinemovie.repos.UserRepo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    public UUID create(UserRegisterDto dto) {
        if (userRepo.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("User with the following email already exists");
        }
        if (userRepo.existsByLogin(dto.login())) {
            throw new IllegalArgumentException("User with the following login already exists");
        }

        User user = User.builder()
            .email(dto.email())
            .password(dto.password())
            .login(dto.login())
            .name(dto.name())
            .state(dto.state())
            .build();

        user = userRepo.saveAndFlush(user);
        return user.getUserId();
    }

    public List<UserResponseDto> getAll() {
        return userRepo.findAll().stream()
            .map(this::toResponseDto)
            .toList();
    }

    public UserResponseDto get(UUID userId) {
        return toResponseDto(getEntity(userId));
    }

    public UserResponseDto update(UUID userId, UserUpdateDto dto) {
        User user = getEntity(userId);

        if (dto.login() != null && !dto.login().equals(user.getLogin())) {
            userRepo.findByLogin(dto.login()).ifPresent(existing -> {
                if (!existing.getUserId().equals(userId)) {
                    throw new IllegalArgumentException("User with the following login already exists");
                }
            });
            user.setLogin(dto.login());
        }

        if (dto.userName() != null) {
            user.setName(dto.userName());
        }
        if (dto.password() != null) {
            user.setPassword(dto.password());
        }

        userRepo.saveAndFlush(user);
        return toResponseDto(user);
    }

    public void delete(UUID userId) {
        User user = getEntity(userId);
        userRepo.delete(user);
    }

    public User getEntity(UUID userId) {
        return userRepo.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " is not found"));
    }

    private UserResponseDto toResponseDto(User u) {
        return UserResponseDto.builder()
            .userId(u.getUserId())
            .login(u.getLogin())
            .userName(u.getName())
            .state(u.isState())
            .build();
    }

    public UserTestDto toTestDto(User u) {
        return UserTestDto.builder()
            .id(u.getUserId())
            .email(u.getEmail())
            .password(u.getPassword())
            .login(u.getLogin())
            .name(u.getName())
            .state(u.isState())
            .build();
    }

    public UserProfileDto toProfileDto(User u) {
        return UserProfileDto.builder()
            .userId(u.getUserId())
            .login(u.getLogin())
            .email(u.getEmail())
            .userName(u.getName())
            .state(u.isState())
            .build();
    }
}
