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

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "directors", columnDefinition = "text[]")
    private List<String> directors;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "genres", columnDefinition = "text[]", nullable = false)
    private List<String> genres;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "actors", columnDefinition = "text[]")
    private List<String> actors;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "regions", columnDefinition = "text[]")
    private List<String> regions;

    @Column(name = "title_name", nullable = false, columnDefinition = "varchar")
    private String titleName;

    @Column(name = "overview", columnDefinition = "text")
    private String overview;

    @Column(name = "release_year")
    private short releaseYear;

    @Column(name = "rating")
    private byte rating;

    @Column(name = "id_tmdb")
    private int idTmdb;
}
