package com.B0cka.service.impl;

import com.B0cka.dto.FrontPostsRequest;
import com.B0cka.dto.PostsResponse;
import com.B0cka.model.Post;
import com.B0cka.repository.PostsRepository;
import com.B0cka.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostsRepository postsRepository;

    @Override
    public Post createPost(FrontPostsRequest frontPostsRequest){
        log.info("Create post in service with params: {}", frontPostsRequest);

        Post post = Post.builder()
                .text(frontPostsRequest.getText().trim())
                .title(frontPostsRequest.getTitle().trim())
                .tags(frontPostsRequest.getTags())
                .likesCount(0L)
                .commentsCount(0L)
                .build();

        return postsRepository.save(post);
    }

    @Override
    public Post updatePost(Long id, FrontPostsRequest req) {
        log.info("Update post id={} with new values", id);

        Post existing = postsRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Post with id={} not found", id);
                    return new IllegalArgumentException("Post not found: id=" + id);
                });

        existing.setTitle(req.getTitle().trim());
        existing.setText(req.getText().trim());
        existing.setTags(req.getTags());

        return postsRepository.save(existing);
    }

    @Override
    @Transactional
    public void savePostImage(Long id, MultipartFile image){
        log.info("Save post image for id={}", id);

        try {
            postsRepository.updateImage(id, image.getBytes());
        } catch (IOException e) {
            throw new IllegalStateException("Error in saving img: ", e);
        }

        log.info("Image successfully updated for post id={}", id);
    }

    @Override
    public byte[] getPostImage(Long id){
        log.info("Get image for post id={}", id);
        if (id == null) {
            throw new IllegalArgumentException("Post id can't be null");
        }
        if (!postsRepository.existsById(id)) {
            throw new IllegalArgumentException("Post not found");
        }
        return postsRepository.findById(id)
                .map(Post::getImage)
                .orElse(null);
    }

    @Override
    @Transactional
    public Long incrementLikes(Long id) {
        log.info("Increment likes for post id={}", id);
        if (id == null) {
            throw new IllegalArgumentException("Post id can't be null");
        }
        if (!postsRepository.existsById(id)) {
            throw new IllegalArgumentException("Post id=" + id + " not found");
        }

        postsRepository.incrementLikes(id);

        return postsRepository.findById(id)
                .map(Post::getLikesCount)
                .orElse(0L);
    }

    public List<Post> getAll() {
        log.info("Retrieve all posts");

        return postsRepository.findAll();
    }

    @Override
    public Post getById(Long id) {
        log.info("Retrieve post by id={}", id);
        if (id == null) {
            throw new IllegalArgumentException("Post id can't be null");
        }
        return postsRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("getById(): post id={} not found", id);
                    return new IllegalArgumentException("Post not found: id=" + id);
                });
    }

    @Override
    public void deletePostById(Long id) {
        log.info("Deleting post id={}", id);
        if (id == null) {
            throw new IllegalArgumentException("Post id can't be null");
        }
        if (!postsRepository.existsById(id)) {
            throw new IllegalArgumentException("Post not found: id=" + id);
        }
        postsRepository.deleteById(id);
        log.info("Deleted post id={}", id);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    public PostsResponse getPagedPosts(String search, int pageNumber, int pageSize) {
        log.info("Find posts: search={}, page={}, size={}", search, pageNumber, pageSize);

        int page = Math.max(pageNumber - 1, 0);
        int size = (pageSize < 1 || pageSize > 100) ? 10 : pageSize;

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Post> postPage;

        if (search != null && !search.isBlank()) {
            postPage = postsRepository.findAllByTitleContainingIgnoreCase(search.trim(), pageable);
        } else {
            postPage = postsRepository.findAll(pageable);
        }

        List<Post> content = postPage.getContent();
        content.forEach(p -> {
            if (p.getText() != null && p.getText().length() > 128) {
                p.setText(p.getText().substring(0, 128) + "…");
            }
        });

        return new PostsResponse(
                content,
                postPage.hasPrevious(),
                postPage.hasNext(),
                postPage.getTotalPages()
        );
    }
}