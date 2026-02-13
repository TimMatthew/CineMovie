package org.ukma.spring.cinemovie.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ukma.spring.cinemovie.dto.user.UserProfileDto;
import org.ukma.spring.cinemovie.dto.user.UserRegisterDto;
import org.ukma.spring.cinemovie.dto.user.UserResponseDto;
import org.ukma.spring.cinemovie.dto.user.UserTestDto;
import org.ukma.spring.cinemovie.dto.user.UserUpdateDto;
import org.ukma.spring.cinemovie.entities.User;
import org.ukma.spring.cinemovie.repos.UserRepo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserService userService;

    @Test
    void create_shouldSaveUserAndReturnId() {
        var id = UUID.randomUUID();

        var dto = UserRegisterDto.builder()
            .email("a@b.com")
            .password("pass")
            .login("login1")
            .name("Name")
            .state(true)
            .build();

        when(userRepo.existsByEmail(dto.email())).thenReturn(false);
        when(userRepo.existsByLogin(dto.login())).thenReturn(false);
        when(userRepo.saveAndFlush(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setUserId(id);
            return u;
        });

        UUID result = userService.create(dto);

        assertEquals(id, result);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepo).saveAndFlush(captor.capture());

        User saved = captor.getValue();
        assertEquals(dto.email(), saved.getEmail());
        assertEquals(dto.password(), saved.getPassword());
        assertEquals(dto.login(), saved.getLogin());
        assertEquals(dto.name(), saved.getName());
        assertEquals(dto.state(), saved.isState());
    }

    @Test
    void getAll_shouldReturnMappedDtos() {
        var u1 = User.builder()
            .userId(UUID.randomUUID())
            .email("u1@a.com")
            .password("p1")
            .login("l1")
            .name("N1")
            .state(true)
            .build();

        var u2 = User.builder()
            .userId(UUID.randomUUID())
            .email("u2@a.com")
            .password("p2")
            .login("l2")
            .name("N2")
            .state(false)
            .build();

        when(userRepo.findAll()).thenReturn(List.of(u1, u2));

        List<UserResponseDto> result = userService.getAll();

        assertEquals(2, result.size());
        assertEquals(u1.getUserId(), result.getFirst().userId());
        assertEquals(u1.getLogin(), result.getFirst().login());
        assertEquals(u1.getName(), result.get(0).userName());
        assertEquals(u1.isState(), result.get(0).state());

        assertEquals(u2.getUserId(), result.get(1).userId());
        assertEquals(u2.getLogin(), result.get(1).login());
        assertEquals(u2.getName(), result.get(1).userName());
        assertEquals(u2.isState(), result.get(1).state());
    }

    @Test
    void get_shouldReturnMappedDto() {
        var id = UUID.randomUUID();
        var user = User.builder()
            .userId(id)
            .email("u@a.com")
            .password("p")
            .login("login")
            .name("Name")
            .state(true)
            .build();

        when(userRepo.findById(id)).thenReturn(Optional.of(user));

        UserResponseDto result = userService.get(id);

        assertEquals(id, result.userId());
        assertEquals("login", result.login());
        assertEquals("Name", result.userName());
        assertTrue(result.state());
    }

    @Test
    void update_shouldPatchUserAndReturnMappedDto() {
        var id = UUID.randomUUID();

        var existing = User.builder()
            .userId(id)
            .email("u@a.com")
            .password("oldPass")
            .login("oldLogin")
            .name("Old Name")
            .state(true)
            .build();

        var dto = UserUpdateDto.builder()
            .login("newLogin")
            .password("newPass")
            .userName("New Name")
            .build();

        when(userRepo.findById(id)).thenReturn(Optional.of(existing));
        when(userRepo.findByLogin("newLogin")).thenReturn(Optional.empty());
        when(userRepo.saveAndFlush(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDto result = userService.update(id, dto);

        assertEquals(id, result.userId());
        assertEquals("newLogin", result.login());
        assertEquals("New Name", result.userName());
        assertTrue(result.state());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepo).saveAndFlush(captor.capture());

        User saved = captor.getValue();
        assertEquals("newLogin", saved.getLogin());
        assertEquals("newPass", saved.getPassword());
        assertEquals("New Name", saved.getName());
    }

    @Test
    void delete_shouldRemoveUser() {
        var id = UUID.randomUUID();
        var user = User.builder()
            .userId(id)
            .email("u@a.com")
            .password("p")
            .login("login")
            .name("Name")
            .state(true)
            .build();

        when(userRepo.findById(id)).thenReturn(Optional.of(user));

        userService.delete(id);

        verify(userRepo).delete(user);
    }

    @Test
    void getEntity_shouldReturnEntity() {
        var id = UUID.randomUUID();
        var user = User.builder()
            .userId(id)
            .email("u@a.com")
            .password("p")
            .login("login")
            .name("Name")
            .state(true)
            .build();

        when(userRepo.findById(id)).thenReturn(Optional.of(user));

        User result = userService.getEntity(id);

        assertSame(user, result);
    }

    @Test
    void toTestDto_shouldMapAllFields() {
        var id = UUID.randomUUID();
        var user = User.builder()
            .userId(id)
            .email("u@a.com")
            .password("p")
            .login("login")
            .name("Name")
            .state(false)
            .build();

        UserTestDto dto = userService.toTestDto(user);

        assertEquals(id, dto.id());
        assertEquals("u@a.com", dto.email());
        assertEquals("p", dto.password());
        assertEquals("login", dto.login());
        assertEquals("Name", dto.name());
        assertFalse(dto.state());
    }

    @Test
    void toProfileDto_shouldMapAllFields() {
        var id = UUID.randomUUID();
        var user = User.builder()
            .userId(id)
            .email("u@a.com")
            .password("p")
            .login("login")
            .name("Name")
            .state(true)
            .build();

        UserProfileDto dto = userService.toProfileDto(user);

        assertEquals(id, dto.userId());
        assertEquals("u@a.com", dto.email());
        assertEquals("login", dto.login());
        assertEquals("Name", dto.userName());
        assertTrue(dto.state());
    }
}
