package com.example.moviesbymood.repositories;

import com.example.moviesbymood.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import jakarta.transaction.Transactional;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByCommentMovie_MovieIdOrderByCommentCreatedAtDesc(Long movieId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.commentMovie.movieId = :movieId")
    void deleteByCommentMovie_MovieId(Long movieId);

    List<Comment> findAllByOrderByCommentCreatedAtDesc();
}
