package org.ukma.spring.cinemovie.controllers;

import lombok.RequiredArgsConstructor;
import org.ukma.spring.cinemovie.dto.favourites.FavCreateDto;
import org.ukma.spring.cinemovie.dto.favourites.FavResponseDto;
import org.ukma.spring.cinemovie.dto.title.TitleResponseDto;
import org.ukma.spring.cinemovie.services.FavouriteService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("favs")
public class FavouriteController {

    private final FavouriteService fs;

    @PostMapping
    public UUID create(@RequestBody FavCreateDto f){
        return fs.create(f);
    }

    @GetMapping("{id}")
    public List<FavResponseDto> getAll(@PathVariable UUID id){
        return fs.getAll(id);
    }

    @DeleteMapping("{id}")
    public boolean delete(@PathVariable UUID id){
        return fs.delete(id);
    }


}
