package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.ListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListItemRepository extends JpaRepository<ListItem, Long> {
    @Modifying
    @Query("DELETE FROM ListItem lt WHERE lt.movie.id = :movieId")
    void deleteByMovieId(@Param("movieId") Long movieId);

    List<ListItem> findByListId(Long listId);

    @Modifying
    @Query("DELETE FROM ListItem li WHERE li.listId = :listId AND li.movieId = :movieId")
    void deleteByListIdAndMovieId(Long listId, Long movieId);
}
