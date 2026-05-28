package com.example.moviesbymood.repositories;

import com.example.moviesbymood.models.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM movie_actors WHERE actor_id = :actorId", nativeQuery = true)
    void deleteMovieActorsByActorId(@Param("actorId") Long actorId);

    List<Actor> findByActorFullNameContainingIgnoreCase(String namePart);

    @Query("""
       SELECT a
         FROM Actor a
        WHERE LOWER(a.actorFullName) LIKE LOWER(CONCAT('%', :q, '%'))
      """)
    List<Actor> searchByFullName(@Param("q") String q);
}
