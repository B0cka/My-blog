package com.B0cka.service;

import com.B0cka.dto.FrontPostsRequest;
import com.B0cka.dto.PostsResponse;
import com.B0cka.model.Post;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {
    Post createPost(FrontPostsRequest frontPostsRequest);

    Post updatePost(Long id, FrontPostsRequest req);

    void savePostImage(Long id, MultipartFile image);

    byte[] getPostImage(Long id);

    Long incrementLikes(Long id);

    Post getById(Long id);

    void deletePostById(Long id);

    PostsResponse getPagedPosts(String search, int pageNumber, int pageSize);

}
