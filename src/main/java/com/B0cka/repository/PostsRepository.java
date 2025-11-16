package com.B0cka.repository;

import com.B0cka.dto.FrontPostsRequest;
import com.B0cka.model.Posts;

public interface PostsRepository {

    Posts save(Posts posts);

//    void update(Long id, User user);
//
//    void deleteById(Long id);

}
