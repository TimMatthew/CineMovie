package org.ukma.spring.cinemovie.controllers;

import lombok.RequiredArgsConstructor;
import org.ukma.spring.cinemovie.dto.comment.CommentCreateDto;
import org.ukma.spring.cinemovie.dto.comment.CommentResponseDto;
import org.ukma.spring.cinemovie.dto.comment.CommentUpdateDto;
import org.ukma.spring.cinemovie.services.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("comments")
public class CommentController {

    private final CommentService cs;

    @PostMapping
    public UUID create(@RequestBody CommentCreateDto title) {
        return cs.create(title);
    }

    @GetMapping("{id}")
    public CommentResponseDto get(@PathVariable UUID id){
        return cs.get(id);
    }

    @GetMapping
    public List<CommentResponseDto> getAll(){
        return cs.getAll();
    }

    @GetMapping("user/{userId}")
    public List<CommentResponseDto> getAllByUser(@PathVariable UUID userId){
        return cs.getAllByUser(userId);
    }

    @GetMapping("title/{titleId}")
    public List<CommentResponseDto> getAllByTitle(@PathVariable UUID titleId){
        return cs.getAllByTitle(titleId);
    }


    @PutMapping("{id}")
    public CommentResponseDto update(@PathVariable UUID id, @RequestBody CommentUpdateDto dto){
        return cs.update(id, dto);
    }

    @DeleteMapping("{comment}")
    public boolean delete(@PathVariable UUID comment){
        return cs.delete(comment);
    }
}
