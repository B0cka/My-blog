package com.B0cka.service;

import com.B0cka.model.Comment;
import com.B0cka.repository.CommentsRepository;
import com.B0cka.repository.PostsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentsRepository commentsRepository;
    private final PostsRepository postsRepository;

    public List<Comment> findByPostId(Long postId) {
        log.info("Find comments for post id={}", postId);
        if (postId == null) {
            log.error("findByPostId(): postId is null");
            throw new IllegalArgumentException("Post id can't be null");
        }
        ensurePostExists(postId);
        return commentsRepository.findByPostId(postId);
    }

    public Comment findById(Long postId, Long id) {
        log.info("Find comment id={} for post id={}", id, postId);
        if (postId == null || id == null) {
            log.error("findById(): postId or id is null");
            throw new IllegalArgumentException("Post id and comment id can't be null");
        }
        ensurePostExists(postId);

        return commentsRepository.findById(postId, id)
                .orElseThrow(() -> {
                    log.error("Comment not found: postId={}, commentId={}", postId, id);
                    return new IllegalArgumentException(
                            "Comment not found: postId=" + postId + ", commentId=" + id);
                });
    }

    public Comment createComment(Long postId, Comment comment) {
        log.info("Create comment for post id={}, text={}", postId, comment != null ? comment.getText() : null);
        if (postId == null || comment == null) {
            log.error("createComment(): postId or comment is null");
            throw new IllegalArgumentException("Post id and comment must not be null");
        }
        ensurePostExists(postId);
        if (isBlank(comment.getText())) {
            log.error("createComment(): text empty for post id={}", postId);
            throw new IllegalArgumentException("Comment text must not be empty");
        }

        comment.setPostId(postId);
        postsRepository.findById(postId).ifPresent(post -> {
            long newCount = post.getCommentsCount() + 1;
            post.setCommentsCount(newCount);
            post.setId(postId);
            postsRepository.update(post);
            log.info("Updated commentsCount for post id={} -> {}", postId, newCount);
        });

        return commentsRepository.save(comment);
    }

    public Comment updateComment(Long postId, Comment comment) {
        log.info("Update comment id={} for post id={}", comment != null ? comment.getId() : null, postId);
        if (postId == null || comment == null) {
            log.error("updateComment(): null arguments");
            throw new IllegalArgumentException("Post id and comment must not be null");
        }
        ensurePostExists(postId);
        if (comment.getId() == null) {
            log.error("updateComment(): comment id is null for post id={}", postId);
            throw new IllegalArgumentException("Comment id can't be null for update");
        }
        commentsRepository.findById(postId, comment.getId())
                .orElseThrow(() -> {
                    log.error("updateComment(): comment id={} not found for post id={}", comment.getId(), postId);
                    return new IllegalArgumentException("Comment not found for update");
                });
        if (isBlank(comment.getText())) {
            log.error("updateComment(): text blank for comment id={} post id={}", comment.getId(), postId);
            throw new IllegalArgumentException("Comment text can't be blank");
        }

        comment.setPostId(postId);
        return commentsRepository.update(comment);
    }

    public void deleteComment(Long postId, Long id) {
        log.info("Delete comment id={} for post id={}", id, postId);
        if (postId == null || id == null) {
            log.error("deleteComment(): postId or id is null");
            throw new IllegalArgumentException("Post id and comment id can't be null");
        }
        ensurePostExists(postId);
        if (commentsRepository.findById(postId, id).isEmpty()) {
            log.error("deleteComment(): comment id={} not found for post id={}", id, postId);
            throw new IllegalArgumentException("Comment not found");
        }
        commentsRepository.delete(postId, id);
        postsRepository.findById(postId).ifPresent(post -> {
            long current = post.getCommentsCount() != null ? post.getCommentsCount() : 0L;
            if (current > 0) post.setCommentsCount(current - 1);
            postsRepository.update(post);
            log.info("DELETED commentsCount for post id={} -> {}", postId, post.getCommentsCount());
        });

        log.info("Deleted comment id={} for post id={}", id, postId);
    }

    private void ensurePostExists(Long postId) {
        if (postsRepository.findById(postId).isEmpty()) {
            log.error("Post id={} not found", postId);
            throw new IllegalArgumentException("Post not found: id=" + postId);
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}