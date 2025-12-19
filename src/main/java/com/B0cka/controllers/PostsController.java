package com.B0cka.controllers;

import com.B0cka.dto.FrontPostsRequest;
import com.B0cka.dto.PostFullDto;
import com.B0cka.dto.PostsResponse;
import com.B0cka.mapper.PostMapper;
import com.B0cka.model.Post;
import com.B0cka.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Validated
public class PostsController {

    private final PostService postService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostFullDto> createPost(@Valid @RequestBody FrontPostsRequest frontPostsRequest){
        log.info("Create new post with text: {}", frontPostsRequest.getText());
        Post post = postService.createPost(frontPostsRequest);
        log.info("CONTROLLER POST: {}", post);
        return ResponseEntity.status(HttpStatus.CREATED).body(PostMapper.toDto(post));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostFullDto> updatePost(@PathVariable("id") Long id,@Valid @RequestBody FrontPostsRequest request) {
        log.info("Update post id {} with new data: {}", id, request);
        Post post = postService.updatePost(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(PostMapper.toDto(post));
    }

    @PostMapping("/{id}/likes")
    @ResponseStatus(HttpStatus.OK)
    public Long addLike(@PathVariable("id") Long id) {
        return postService.incrementLikes(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePost(@PathVariable("id") Long id) {
        log.info("Deleting post id={}", id);
        postService.deletePostById(id);
    }

    @GetMapping("/{id}")
    public Post getPostById(@PathVariable("id") Long id) {
        log.info("Get post id={}", id);
        return postService.getById(id);
    }

    @PutMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> uploadImage(@PathVariable("id") Long id, @RequestParam("image") MultipartFile image) throws IOException {
        postService.savePostImage(id, image);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getPostImage(@PathVariable("id") Long id) {
        log.info("Get image for post id={}", id);
        return postService.getPostImage(id);
    }

    @GetMapping
    public PostsResponse getPosts(@RequestParam(value = "search", required = false, defaultValue = "") String search,
                                  @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                                  @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {
        return postService.getPagedPosts(search, pageNumber, pageSize);
    }
}
