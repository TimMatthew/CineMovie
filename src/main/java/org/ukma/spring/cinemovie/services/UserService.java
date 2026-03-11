package org.ukma.spring.cinemovie.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ukma.spring.cinemovie.dto.user.*;
import org.ukma.spring.cinemovie.entities.User;
import org.ukma.spring.cinemovie.repos.UserRepo;
import org.ukma.spring.cinemovie.security.JWTUtils;
import org.ukma.spring.cinemovie.security.PasswordUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final PasswordUtils pwdUtils;
    private final JWTUtils jwt;

    public UserResponseDto authenticate(UserAuthDto dto) {
        User user = userRepo.findByLogin(dto.login())
            .orElseThrow(() -> new IllegalArgumentException("User with login " + dto.login() + " not found"));

        if (!pwdUtils.matchPassword(dto.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
        return toResponseDto(user);
    }

    public UUID create(UserRegisterDto dto) {
        if (userRepo.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("User with the following email already exists");
        }
        if (userRepo.existsByLogin(dto.login())) {
            throw new IllegalArgumentException("User with the following login already exists");
        }

        User user = User.builder()
            .email(dto.email())
            .password(pwdUtils.hash(dto.password()))
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
        User user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " is not found"));

        return toResponseDto(user);
    }

    public User getEntity(UUID userId) {
        return userRepo.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " is not found"));
    }

    public UserResponseDto update(UUID userId, UserUpdateDto dto, String token) {
        if(token == null)
            throw new SecurityException("You must be authenticated (please log in into your account)!");

        String authenticatedUserId = jwt.extractUserId(token);


        var user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " is not found"));
        String id = String.valueOf(user.getUserId());

        if(!id.equals(authenticatedUserId))
            throw new SecurityException("You cannot modify another user's account");

        user.setName(dto.userName());
        user.setLogin(dto.login());
        user.setPassword(dto.password());

        userRepo.saveAndFlush(user);
        return toResponseDto(user);
    }

    public boolean delete(UUID userId, String token) {
        String roleFromToken = jwt.extractRole(token);
        String authenticatedUserId = jwt.extractUserId(token);

        User user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " is not found"));
        String id = String.valueOf(user.getUserId());
        boolean isClient = roleFromToken.equals("false");
        boolean idsAreEqual = id.equals(authenticatedUserId);

        if(isClient && !idsAreEqual)
            throw new SecurityException("You cannot delete another user's account");

        userRepo.delete(user);
        return true;
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
