package org.ukma.spring.cinemovie.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "favourites")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FavouriteTitle {
    @Id
    @GeneratedValue
    @Column(name = "fav_id", nullable = false)
    private UUID favId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "title_id", nullable = false)
    private Title title;
}
