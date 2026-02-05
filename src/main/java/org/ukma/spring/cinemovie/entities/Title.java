package org.ukma.spring.cinemovie.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "title")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Title {

    @Id
    @GeneratedValue
    @Column(name = "title_id", nullable = false)
    private UUID titleId;

    @Column(name = "tmdb_id", nullable = false)
    private String tmdbId;

    @Column(name = "title_name", nullable = false)
    private String titleName;

    @Column(name = "overview", columnDefinition = "text")
    private String overview;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "keywords", columnDefinition = "text[]")
    private List<String> keywords;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "genres", columnDefinition = "text[]", nullable = false)
    private List<String> genres;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "actors", columnDefinition = "text[]")
    private List<String> actors;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "director")
    private List<String> director;

    @Column(name = "release_year")
    private short releaseYear;

    @Column(name = "rating")
    private byte rating;

    @Column(name = "tmdb_image_url")
    private String imageUrl;
}