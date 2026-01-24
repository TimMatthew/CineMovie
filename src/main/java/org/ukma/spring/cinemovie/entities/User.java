package org.ukma.spring.cinemovie.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user_t")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "email", nullable = false, length = 40)
    private String email;

    @Column(name = "password", nullable = false, length = 40)
    private String password;

    @Column(name = "login", nullable = false, length = 40)
    private String login;

    @Column(name = "name", length = 40)
    private String name;

    @Column(name = "state", nullable = false)
    private boolean state;
}
