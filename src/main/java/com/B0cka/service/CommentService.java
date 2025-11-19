package com.B0cka.service;

import com.B0cka.model.Comment;
import com.B0cka.repository.CommentsRepository;
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

    public List<Comment> findByPostId(Long postId) {
        log.info("Find comments for post id={}", postId);
        return commentsRepository.findByPostId(postId);
    }

    public Comment findById(Long postId, Long id) {
        log.info("Find comment id={} for post id={}", id, postId);
        return commentsRepository.findById(postId, id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Comment not found: postId=" + postId + ", commentId=" + id));
    }

    public Comment createComment(Long postId, Comment comment) {
        comment.setPostId(postId);
        log.info("Create comment for post id={}, text={}", postId, comment.getText());
        return commentsRepository.save(comment);
    }

    public Comment updateComment(Long postId, Comment comment) {
        comment.setPostId(postId);
        log.info("Update comment id={} for post id={}", comment.getId(), postId);
        return commentsRepository.update(comment);
    }

    public void deleteComment(Long postId, Long id) {
        log.info("Delete comment id={} for post id={}", id, postId);
        commentsRepository.delete(postId, id);
    }
}