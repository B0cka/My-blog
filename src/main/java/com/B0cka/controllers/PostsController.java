package com.B0cka.controllers;

import com.B0cka.dto.FrontPostsRequest;
import com.B0cka.dto.PostsResponse;
import com.B0cka.model.Post;
import com.B0cka.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Post updatePost(@PathVariable("id") Long id, @RequestBody FrontPostsRequest request) {
        log.info("Update post id {} with new data: {}", id, request);
        return postService.updatePost(id, request);
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

    @GetMapping
    public List<Post> getAllPosts() {
        log.info("Get all posts");
        return postService.getAll();
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
