package com.B0cka.repository;

import com.B0cka.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface PostsRepository extends PagingAndSortingRepository<Post, Long>, CrudRepository<Post, Long> {

    Page<Post> findAllByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Modifying
    @Query("UPDATE posts SET likes_count = likes_count + 1 WHERE id = :id")
    void incrementLikes(@Param("id") Long id);

    @Modifying
    @Query("UPDATE posts SET image = :img WHERE id = :id")
    void updateImage(@Param("id") Long id, @Param("img") byte[] img);

    List<Post> findAll();

    Page<Post> findAll(Pageable pageable);
}