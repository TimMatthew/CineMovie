package org.ukma.spring.cinemovie.repos;

import org.ukma.spring.cinemovie.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface CommentRepo extends JpaRepository<Comment, UUID> {
    
}
