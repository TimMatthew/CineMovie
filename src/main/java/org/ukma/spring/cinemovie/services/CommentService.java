package org.ukma.spring.cinemovie.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepo commentRepo;
    private final UserRepo userRepo;
    private final TitleRepo titleRepo;

    public UUID create(CommentCreateDto dto) {
        UUID userId = UUID.fromString(dto.userId());
        UUID titleId = UUID.fromString(dto.titleId());

        User user = userRepo.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User with id " + dto.userId() + " is not found"));

        Title title = titleRepo.findById(titleId)
            .orElseThrow(() -> new IllegalArgumentException("Title with id " + dto.titleId() + " is not found"));

        validateRating(dto.rating());

        Comment comment = Comment.builder()
            .user(user)
            .title(title)
            .creationDate(OffsetDateTime.now())
            .rating((byte) dto.rating())
            .info(dto.info())
            .build();

        comment = commentRepo.saveAndFlush(comment);
        return comment.getCommentId();
    }

    public CommentResponseDto get(UUID id) {
        Comment comment = commentRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Comment with id " + id + " is not found"));

        return toDto(comment);
    }

    public List<CommentResponseDto> getAll() {
        return commentRepo.findAll().stream()
            .map(this::toDto)
            .toList();
    }

    public List<CommentResponseDto> getAllByUser(UUID userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " is not found"));

        return commentRepo.findAllByUser(user).stream()
            .map(this::toDto)
            .toList();
    }

    public List<CommentResponseDto> getAllByTitle(UUID titleId) {
        Title title = titleRepo.findById(titleId)
            .orElseThrow(() -> new IllegalArgumentException("Title with id " + titleId + " is not found"));

        return commentRepo.findAllByTitle(title).stream()
            .map(this::toDto)
            .toList();
    }

    public CommentResponseDto update(UUID commentId, CommentUpdateDto dto) {
        Comment comment = commentRepo.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment with id " + commentId + " is not found"));

        validateRating(dto.rating());

        comment.setRating((byte) dto.rating());
        comment.setInfo(dto.info());
        comment.setCreationDate(OffsetDateTime.now());

        comment = commentRepo.saveAndFlush(comment);
        return toDto(comment);
    }

    public boolean delete(UUID commentId) {
        Comment comment = commentRepo.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment with id " + commentId + " is not found"));

        commentRepo.delete(comment);
        return true;
    }

    private CommentResponseDto toDto(Comment c) {
        return CommentResponseDto.builder()
            .id(c.getCommentId())
            .userId(c.getUser().getUserId())
            .titleId(c.getTitle().getTitleId())
            .creationDate(c.getCreationDate())
            .rating(c.getRating())
            .info(c.getInfo())
            .build();
    }

    private void validateRating(int rating) {
        if (rating < 0 || rating > 10) {
            throw new IllegalArgumentException("Rating must be in range 0..10, but got: " + rating);
        }
    }
}
