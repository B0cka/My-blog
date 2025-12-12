package com.B0cka.service;

import com.B0cka.dto.FrontPostsRequest;
import com.B0cka.dto.PostsResponse;
import com.B0cka.model.Post;
import com.B0cka.repository.PostsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostsRepository postsRepository;

    public Post createPost(FrontPostsRequest frontPostsRequest){
        log.info("Create post in service with params: {}", frontPostsRequest);

        if (frontPostsRequest == null) {
            log.error("FrontPostsRequest is null");
            throw new IllegalArgumentException("Request body can't be null");
        }
        if (isBlank(frontPostsRequest.getTitle()) || isBlank(frontPostsRequest.getText())) {
            log.error("Empty title or text in createPost()");
            throw new IllegalArgumentException("Title and text must not be empty");
        }

        Post post = Post.builder()
                .text(frontPostsRequest.getText().trim())
                .title(frontPostsRequest.getTitle().trim())
                .tags(frontPostsRequest.getTags())
                .likesCount(0L)
                .commentsCount(0L)
                .build();

        return postsRepository.save(post);
    }

    public Post updatePost(Long id, FrontPostsRequest req) {
        log.info("Update post id={} with new values", id);

        if (id == null) {
            log.error("updatePost(): id is null");
            throw new IllegalArgumentException("Post id can't be null");
        }
        if (req == null) {
            log.error("updatePost(): request body is null");
            throw new IllegalArgumentException("Request can't be null");
        }

        Post existing = postsRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Post with id={} not found", id);
                    return new IllegalArgumentException("Post not found: id=" + id);
                });

        if (isBlank(req.getTitle()) || isBlank(req.getText())) {
            log.error("updatePost(): title/text empty for id={}", id);
            throw new IllegalArgumentException("Title and text must not be empty");
        }

        existing.setTitle(req.getTitle().trim());
        existing.setText(req.getText().trim());
        existing.setTags(req.getTags());

        return postsRepository.update(existing);
    }

    public void savePostImage(Long id, MultipartFile image) throws IOException {
        log.info("Save post image for id={}", id);

        if (id == null) {
            log.error("Image upload: post id is null");
            throw new IllegalArgumentException("Post id can't be null");
        }
        if (image == null || image.isEmpty()) {
            log.error("Image upload: no file received");
            throw new IllegalArgumentException("Image file must not be null or empty");
        }
        if (postsRepository.findById(id).isEmpty()) {
            log.error("Image upload: post id={} not found", id);
            throw new IllegalArgumentException("Post id=" + id + " not found");
        }

        postsRepository.updateImg(image.getBytes(), id);
        log.info("Image successfully updated for post id={}", id);
    }

    public byte[] getPostImage(Long id){
        log.info("Get image for post id={}", id);
        if (id == null) {
            log.error("getPostImage(): id is null");
            throw new IllegalArgumentException("Post id can't be null");
        }
        return postsRepository.getPostImage(id);
    }

    public Long incrementLikes(Long id) {
        log.info("Increment likes for post id={}", id);
        if (id == null) {
            log.error("incrementLikes(): id is null");
            throw new IllegalArgumentException("Post id can't be null");
        }
        if (postsRepository.findById(id).isEmpty()) {
            log.error("incrementLikes(): post id={} not found", id);
            throw new IllegalArgumentException("Post id=" + id + " not found");
        }
        return postsRepository.incrementLikes(id);
    }

    public List<Post> getAll() {
        log.info("Retrieve all posts");
        return postsRepository.findAll();
    }

    public Post getById(Long id) {
        log.info("Retrieve post by id={}", id);
        if (id == null) {
            log.error("getById(): id is null");
            throw new IllegalArgumentException("Post id can't be null");
        }
        return postsRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("getById(): post id={} not found", id);
                    return new IllegalArgumentException("Post not found: id=" + id);
                });
    }

    public void deletePostById(Long id) {
        log.info("Deleting post id={}", id);
        if (id == null) {
            log.error("deletePostById(): id is null");
            throw new IllegalArgumentException("Post id can't be null");
        }
        if (postsRepository.findById(id).isEmpty()) {
            log.error("deletePostById(): post id={} not found", id);
            throw new IllegalArgumentException("Post not found: id=" + id);
        }
        postsRepository.delete(id);
        log.info("Deleted post id={}", id);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public PostsResponse getPagedPosts(String search, int pageNumber, int pageSize) {
        log.info("Find posts with params: search = {}, pageNumber = {}, pageSize = {}", search, pageNumber, pageSize);

        if (pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = 10; // разумный дефолт
        }

        List<Post> allPosts = postsRepository.findAll();

        if (search != null && !search.isBlank()) {
            String query = search.toLowerCase();
            allPosts = allPosts.stream()
                    .filter(p -> p.getTitle() != null &&
                            p.getTitle().toLowerCase().contains(query))
                    .toList();
        }

        int totalCount = allPosts.size();
        if (totalCount == 0) {
            return new PostsResponse(List.of(), false, false, 1);
        }

        int lastPage = (int) Math.ceil((double) totalCount / pageSize);
        if (pageNumber > lastPage) {
            pageNumber = lastPage;
        }

        int start = (pageNumber - 1) * pageSize;
        int end = Math.min(start + pageSize, totalCount);

        List<Post> page = allPosts.subList(start, end);

        for (Post p : page) {
            if (p.getText() != null && p.getText().length() > 128) {
                p.setText(p.getText().substring(0, 128) + "…");
            }
        }

        boolean hasPrev = pageNumber > 1;
        boolean hasNext = pageNumber < lastPage;

        return new PostsResponse(page, hasPrev, hasNext, lastPage);
    }
}