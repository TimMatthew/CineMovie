package org.ukma.spring.cinemovie.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtils {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String hash(String pwd){
        return passwordEncoder.encode(pwd);
    }

    public boolean matchPassword(String pwd, String hashed){
        return passwordEncoder.matches(pwd, hashed);
    }
}
