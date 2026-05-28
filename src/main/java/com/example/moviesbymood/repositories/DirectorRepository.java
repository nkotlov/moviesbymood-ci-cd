package com.example.moviesbymood.repositories;

import com.example.moviesbymood.models.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DirectorRepository extends JpaRepository<Director, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM movie_directors WHERE director_id = :dirId", nativeQuery = true)
    void deleteMovieDirectorsByDirectorId(@Param("dirId") Long dirId);

    List<Director> findByDirectorFullNameContainingIgnoreCase(String namePart);

    @Query("""
       SELECT d
         FROM Director d
        WHERE LOWER(d.directorFullName) LIKE LOWER(CONCAT('%', :q, '%'))
      """)
    List<Director> searchByFullName(@Param("q") String q);
}
