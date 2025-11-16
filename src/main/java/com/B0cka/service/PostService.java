package com.B0cka.service;

import com.B0cka.dto.FrontPostsRequest;
import com.B0cka.model.Posts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    public Posts createPost(FrontPostsRequest frontPostsRequest){
        log.info("Работа с постом");



    }

}
