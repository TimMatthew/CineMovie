package org.ukma.spring.cinemovie.controllers;

import lombok.RequiredArgsConstructor;
import org.ukma.spring.cinemovie.dto.title.TitleResponseDto;
import org.ukma.spring.cinemovie.dto.title.TitleUpsertDto;
import org.ukma.spring.cinemovie.services.TitleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("titles")
@RequiredArgsConstructor
public class TitleController {

    private final TitleService titleService;

    @PostMapping
    public UUID create(@RequestBody TitleUpsertDto title, @CookieValue(name="jwt") String token) {
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
    public TitleUpsertDto update(@PathVariable UUID id, @RequestBody TitleUpsertDto dto, @CookieValue(name="jwt") String jwtToken) {
        return titleService.update(id, dto);
    }

    @DeleteMapping("{id}")
    public boolean delete(@PathVariable UUID id, @CookieValue(name="jwt") String jwtToken) {
        return titleService.delete(id);
    }
}
