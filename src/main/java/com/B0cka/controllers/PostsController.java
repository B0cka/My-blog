package com.B0cka.controllers;

import com.B0cka.dto.FrontPostsRequest;
import com.B0cka.model.Post;
import com.B0cka.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostsController {

    private final PostService postService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Post createPost(@RequestBody FrontPostsRequest frontPostsRequest){
        log.info("Create new post with text: {}", frontPostsRequest.getText());

        return postService.createPost(frontPostsRequest);
    }

}
