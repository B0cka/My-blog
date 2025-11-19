package com.B0cka.service;

import com.B0cka.dto.FrontPostsRequest;
import com.B0cka.model.Post;
import com.B0cka.repository.PostsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
                .likesCount(0L)
                .commentsCount(0L)
                .build());
    }

    public Post updatePost(Long id, FrontPostsRequest req) {
        log.info("Update post id={} with new values", id);
        return postsRepository.update(
                Post.builder()
                        .id(id)
                        .title(req.getTitle())
                        .text(req.getText())
                        .tags(req.getTags())
                        .build());
    }

    public void savePostImage(Long id, MultipartFile image) throws IOException {
        byte[] bytes = image.getBytes();
        postsRepository.updateImg(bytes, id);
        log.info("Image updated for post id={}", id);
    }

    public byte[] getPostImage(Long id){
        log.info("Get image for post id={} in service", id);
        return postsRepository.getPostImage(id);
    }

    public Long incrementLikes(Long id) {
        return postsRepository.incrementLikes(id);
    }

    public List<Post> getAll() {
        log.info("Retrieve all posts");
        return postsRepository.findAll();
    }

    public Post getById(Long id) {
        log.info("Retrieve post by id={}", id);
        return postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: id=" + id));
    }

    public void deletePostById(Long id) {
        log.info("Deleting post id={}", id);
        postsRepository.delete(id);
    }



}
