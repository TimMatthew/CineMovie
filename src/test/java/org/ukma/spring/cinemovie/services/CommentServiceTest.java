package org.ukma.spring.cinemovie.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ukma.spring.cinemovie.dto.comment.CommentCreateDto;
import org.ukma.spring.cinemovie.dto.comment.CommentResponseDto;
import org.ukma.spring.cinemovie.dto.comment.CommentUpdateDto;
import org.ukma.spring.cinemovie.entities.Comment;
import org.ukma.spring.cinemovie.entities.Title;
import org.ukma.spring.cinemovie.entities.User;
import org.ukma.spring.cinemovie.repos.CommentRepo;
import org.ukma.spring.cinemovie.repos.TitleRepo;
import org.ukma.spring.cinemovie.repos.UserRepo;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock private CommentRepo commentRepo;
    @Mock private UserRepo userRepo;
    @Mock private TitleRepo titleRepo;

    @InjectMocks private CommentService commentService;

    private UUID userId;
    private UUID titleId;
    private UUID commentId;

    private User user;
    private Title title;
    private Comment comment;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        titleId = UUID.randomUUID();
        commentId = UUID.randomUUID();

        user = User.builder()
            .userId(userId)
            .build();

        title = Title.builder()
            .titleId(titleId)
            .build();

        comment = Comment.builder()
            .commentId(commentId)
            .user(user)
            .title(title)
            .creationDate(OffsetDateTime.now().minusDays(1))
            .rating((byte) 7)
            .info("Old comment")
            .build();
    }

    @Test
    void create_shouldSaveAndReturnId() {
        CommentCreateDto dto = new CommentCreateDto(
            userId.toString(),
            titleId.toString(),
            (byte) 10,
            "Nice movie"
        );

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(titleRepo.findById(titleId)).thenReturn(Optional.of(title));

        Comment saved = Comment.builder()
            .commentId(commentId)
            .user(user)
            .title(title)
            .creationDate(OffsetDateTime.now())
            .rating((byte) 10)
            .info("Nice movie")
            .build();

        when(commentRepo.saveAndFlush(any(Comment.class))).thenReturn(saved);

        UUID resId = commentService.create(dto);

        assertEquals(commentId, resId);

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepo).saveAndFlush(captor.capture());
        Comment toSave = captor.getValue();

        assertEquals(userId, toSave.getUser().getUserId());
        assertEquals(titleId, toSave.getTitle().getTitleId());
        assertEquals((byte) 10, toSave.getRating());
        assertEquals("Nice movie", toSave.getInfo());
        assertNotNull(toSave.getCreationDate());
    }

    @Test
    void create_shouldThrow_whenUserNotFound() {
        CommentCreateDto dto = new CommentCreateDto(
            userId.toString(),
            titleId.toString(),
            (byte) 5,
            "Ok"
        );

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> commentService.create(dto)
        );
        assertTrue(ex.getMessage().contains("User with id"));
        verify(commentRepo, never()).saveAndFlush(any());
    }

    @Test
    void create_shouldThrow_whenTitleNotFound() {
        CommentCreateDto dto = new CommentCreateDto(
            userId.toString(),
            titleId.toString(),
            (byte) 5,
            "Ok"
        );

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(titleRepo.findById(titleId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> commentService.create(dto)
        );
        assertTrue(ex.getMessage().contains("Title with id"));
        verify(commentRepo, never()).saveAndFlush(any());
    }

    @Test
    void create_shouldThrow_whenRatingInvalid() {
        CommentCreateDto dto = new CommentCreateDto(
            userId.toString(),
            titleId.toString(),
            (byte) 11,
            "Bad"
        );

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(titleRepo.findById(titleId)).thenReturn(Optional.of(title));

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> commentService.create(dto)
        );
        assertTrue(ex.getMessage().contains("Rating must be in range"));
        verify(commentRepo, never()).saveAndFlush(any());
    }

    @Test
    void get_shouldReturnDto() {
        when(commentRepo.findById(commentId)).thenReturn(Optional.of(comment));

        CommentResponseDto res = commentService.get(commentId);

        assertEquals(commentId, res.id());
        assertEquals(userId, res.userId());
        assertEquals(titleId, res.titleId());
        assertEquals((byte) 7, res.rating());
        assertEquals("Old comment", res.info());
        assertNotNull(res.creationDate());
    }

    @Test
    void get_shouldThrow_whenCommentNotFound() {
        when(commentRepo.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> commentService.get(commentId));
    }

    @Test
    void getAll_shouldReturnList() {
        when(commentRepo.findAll()).thenReturn(List.of(comment));

        List<CommentResponseDto> res = commentService.getAll();

        assertEquals(1, res.size());
        assertEquals(commentId, res.getFirst().id());
    }

    @Test
    void getAllByUser_shouldReturnList() {
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepo.findAllByUser(user)).thenReturn(List.of(comment));

        List<CommentResponseDto> res = commentService.getAllByUser(userId);

        assertEquals(1, res.size());
        assertEquals(userId, res.getFirst().userId());
    }

    @Test
    void getAllByUser_shouldThrow_whenUserNotFound() {
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> commentService.getAllByUser(userId));
        verify(commentRepo, never()).findAllByUser(any());
    }

    @Test
    void getAllByTitle_shouldReturnList() {
        when(titleRepo.findById(titleId)).thenReturn(Optional.of(title));
        when(commentRepo.findAllByTitle(title)).thenReturn(List.of(comment));

        List<CommentResponseDto> res = commentService.getAllByTitle(titleId);

        assertEquals(1, res.size());
        assertEquals(titleId, res.getFirst().titleId());
    }

    @Test
    void getAllByTitle_shouldThrow_whenTitleNotFound() {
        when(titleRepo.findById(titleId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> commentService.getAllByTitle(titleId));
        verify(commentRepo, never()).findAllByTitle(any());
    }

    @Test
    void update_shouldUpdateFieldsAndReturnDto() {
        OffsetDateTime oldDate = comment.getCreationDate();

        CommentUpdateDto dto = new CommentUpdateDto(user, (byte) 9, "Updated");

        when(commentRepo.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepo.saveAndFlush(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));

        CommentResponseDto res = commentService.update(commentId, dto);

        assertEquals(commentId, res.id());
        assertEquals((byte) 9, res.rating());
        assertEquals("Updated", res.info());
        assertNotNull(res.creationDate());

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepo).saveAndFlush(captor.capture());
        Comment updated = captor.getValue();

        assertEquals((byte) 9, updated.getRating());
        assertEquals("Updated", updated.getInfo());
        assertTrue(updated.getCreationDate().isAfter(oldDate));
    }

    @Test
    void update_shouldThrow_whenCommentNotFound() {
        CommentUpdateDto dto = new CommentUpdateDto(user, (byte) 5, "X");
        when(commentRepo.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> commentService.update(commentId, dto));
        verify(commentRepo, never()).saveAndFlush(any());
    }

    @Test
    void update_shouldThrow_whenRatingInvalid() {
        CommentUpdateDto dto = new CommentUpdateDto(user, (byte) -1, "X");
        when(commentRepo.findById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(IllegalArgumentException.class, () -> commentService.update(commentId, dto));
        verify(commentRepo, never()).saveAndFlush(any());
    }

    @Test
    void delete_shouldDeleteAndReturnTrue() {
        when(commentRepo.findById(commentId)).thenReturn(Optional.of(comment));

        boolean res = commentService.delete(commentId);

        assertTrue(res);
        verify(commentRepo).delete(comment);
    }

    @Test
    void delete_shouldThrow_whenCommentNotFound() {
        when(commentRepo.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> commentService.delete(commentId));
        verify(commentRepo, never()).delete(any());
    }
}
