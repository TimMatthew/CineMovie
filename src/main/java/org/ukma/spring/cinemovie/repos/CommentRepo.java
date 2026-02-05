package org.ukma.spring.cinemovie.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ukma.spring.cinemovie.entities.Comment;
import org.ukma.spring.cinemovie.entities.Title;
import org.ukma.spring.cinemovie.entities.User;

import java.util.List;
import java.util.UUID;


public interface CommentRepo extends JpaRepository<Comment, UUID> {

    List<Comment> findAllByUser(User user);

    List<Comment> findAllByTitle(Title title);

}
