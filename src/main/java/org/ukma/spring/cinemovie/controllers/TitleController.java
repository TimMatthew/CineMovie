package org.ukma.spring.cinemovie.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.ukma.spring.cinemovie.dto.title.TitleResponseDto;
import org.ukma.spring.cinemovie.dto.title.TitleUpsertDto;
import org.ukma.spring.cinemovie.security.JwtAccessValidator;
import org.ukma.spring.cinemovie.services.TitleService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("titles")
@RequiredArgsConstructor
public class TitleController {

    private final TitleService titleService;
    private final JwtAccessValidator jwtAccessValidator;

    @PostMapping
    public UUID create(@RequestBody TitleUpsertDto title, HttpServletRequest request) {
        jwtAccessValidator.requireAdmin(request);
        return titleService.create(title);
    }

    @GetMapping("{id}")
    public TitleResponseDto get(@PathVariable UUID id) {
        return titleService.get(id);
    }

    @GetMapping
    public List<TitleResponseDto> getAll() {
        return titleService.getAll();
    }

    @PutMapping("{id}")
    public TitleUpsertDto update(@PathVariable UUID id,
                                 @RequestBody TitleUpsertDto dto,
                                 HttpServletRequest request) {
        jwtAccessValidator.requireAdmin(request);
        return titleService.update(id, dto);
    }

    @DeleteMapping("{id}")
    public boolean delete(@PathVariable UUID id, HttpServletRequest request) {
        jwtAccessValidator.requireAdmin(request);
        return titleService.delete(id);
    }
}