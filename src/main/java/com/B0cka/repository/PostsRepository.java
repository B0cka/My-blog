package com.B0cka.repository;

import com.B0cka.model.Post;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostsRepository extends CrudRepository<Post, Long> {

    List<Post> findAllByTitleContainingIgnoreCase(String title);

    @Modifying
    @Query("UPDATE posts SET likes_count = likes_count + 1 WHERE id = :id")
    void incrementLikes(@Param("id") Long id);

    @Modifying
    @Query("UPDATE posts SET image = :img WHERE id = :id")
    void updateImage(@Param("id") Long id, @Param("image") byte[] img);

    @Override
    List<Post> findAll();

}