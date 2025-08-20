package com.ptit_intern.themoviedb.repository;

import com.ptit_intern.themoviedb.entity.UserList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserListRepository extends JpaRepository<UserList,Long> {
    boolean existsByUserIdAndName(Long userId,String name);
    boolean existsByUserIdAndNameAndIdNot(Long userId,String name,Long id);
    List<UserList> findByUserId(Long userId);

    @Query("SELECT COUNT(li) > 0 FROM ListItem li WHERE li.userList.user.id = :userId AND li.movie.id = :movieId")
    boolean existsMovieInUserLists(@Param("userId") Long userId, @Param("movieId") Long movieId);
}
