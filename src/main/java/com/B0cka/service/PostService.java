package com.B0cka.service;

import com.B0cka.dto.FrontPostsRequest;
import com.B0cka.model.Post;
import com.B0cka.repository.PostsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostsRepository postsRepository;

    public Post createPost(FrontPostsRequest frontPostsRequest){
        log.info("Create post in service with params: {}", frontPostsRequest);

        return postsRepository.save(Post.builder()
                .text(frontPostsRequest.getText())
                .title(frontPostsRequest.getTitle())
                .tags(frontPostsRequest.getTags())
                .build());
    }

}
