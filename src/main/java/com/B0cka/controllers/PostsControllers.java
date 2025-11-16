package com.B0cka.controllers;

import com.B0cka.dto.FrontPostsRequest;
import com.B0cka.model.Posts;
import com.B0cka.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostsControllers {

    private final PostService postService;

    @PostMapping()
    public Posts createPost(@RequestBody FrontPostsRequest frontPostsRequest){
        log.info("Create new post with text: {}", frontPostsRequest.getText());

        return postService.createPost(frontPostsRequest);
    }

}
