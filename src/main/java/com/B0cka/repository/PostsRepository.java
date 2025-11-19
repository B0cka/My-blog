package com.B0cka.repository;

import com.B0cka.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostsRepository {

    Post save(Post post);
    List<Post> findAll();
    void updateImg(byte[] bytes, Long id);
    byte[] getPostImage(Long id);
    Optional<Post> findById(Long id);
    Post update(Post post);
    void delete(Long id);
    Long incrementLikes(Long id);
}
