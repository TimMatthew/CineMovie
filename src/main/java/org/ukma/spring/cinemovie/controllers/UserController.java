package org.ukma.spring.cinemovie.controllers;

import lombok.RequiredArgsConstructor;
import org.ukma.spring.cinemovie.dto.user.*;
import org.ukma.spring.cinemovie.security.JWTUtils;
import org.ukma.spring.cinemovie.services.UserService;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserService us;
    private final JWTUtils jwt;

    @PostMapping("login")
    public ResponseEntity<?> authenticate(@RequestBody UserAuthDto dto){

        var user = us.authenticate(dto);
        var token = jwt.generateToken(user);

        ResponseCookie cookie = ResponseCookie.from("jwt", token)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(60 * 60) // 1 hour
            .sameSite("Lax")
            .build();

        return ResponseEntity
            .ok()
            .header("Set-Cookie", cookie.toString())
            .body(Map.of("token", token));
    }

    @PostMapping("logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(0)
            .build();

        return ResponseEntity.ok()
            .header("Set-Cookie", cookie.toString())
            .body(Map.of("message", "Logged out"));
    }

    @GetMapping("profile")
    public ResponseEntity<?> getCurrentUser(@CookieValue("jwt") String token) {

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(401).body("No JWT cookie found");
        }
        var userId = jwt.extractUserId(token);
        var user = us.toProfileDto(us.getEntity(UUID.fromString(userId)));
        return ResponseEntity.ok(user);
    }

    @PostMapping()
    public UUID create(@RequestBody UserRegisterDto user){
        return us.create(user);
    }
    @GetMapping()
    public List<UserResponseDto> getAllUsers(){
        return us.getAll();
    }

    @GetMapping("{id}")
    public UserResponseDto getUser(@PathVariable UUID id){
        return us.get(id);
    }

    @PutMapping("{id}")
    public UserResponseDto update(@PathVariable UUID id, @RequestBody UserUpdateDto dto, @CookieValue("jwt") String token){
        return us.update(id, dto, token);
    }

    @DeleteMapping("{id}")
    public boolean delete(@PathVariable UUID id, @CookieValue("jwt") String token){
        return us.delete(id, token);
    }
}
