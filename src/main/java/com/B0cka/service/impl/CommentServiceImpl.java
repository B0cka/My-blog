package com.B0cka.service.impl;

import com.B0cka.dto.CommentRequestDto;
import com.B0cka.model.Comment;
import com.B0cka.repository.CommentsRepository;
import com.B0cka.repository.PostsRepository; // Предполагаю, что он тоже переписан
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements com.B0cka.service.CommentService {

    private final CommentsRepository commentsRepository;
    private final PostsRepository postsRepository;

    @Override
    public List<Comment> findByPostId(Long postId) {
        log.info("Find comments for post id={}", postId);
        ensurePostExists(postId);
        return commentsRepository.findAllByPostId(postId);
    }

    @Override
    public Comment findById(Long postId, Long id) {
        ensurePostExists(postId);
        return commentsRepository.findByIdAndPostId(id, postId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found or does not belong to post"));
    }

    @Override
    public Comment createComment(Long postId, CommentRequestDto commentDto) {
        ensurePostExists(postId);
        if (isBlank(commentDto.getText())) {
            throw new IllegalArgumentException("Comment text must not be empty");
        }

        postsRepository.findById(postId).ifPresent(post -> {
            long newCount = (post.getCommentsCount() == null ? 0 : post.getCommentsCount()) + 1;
            post.setCommentsCount(newCount);
            postsRepository.save(post);
        });

        Comment comment = Comment.builder()
                .postId(postId)
                .text(commentDto.getText())
                .build();

        return commentsRepository.save(comment);
    }

    @Override
    public Comment updateComment(Long postId, Comment comment) {
        ensurePostExists(postId);

        Comment existingComment = commentsRepository.findByIdAndPostId(comment.getId(), postId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found for update"));

        if (isBlank(comment.getText())) {
            throw new IllegalArgumentException("Comment text can't be blank");
        }

        existingComment.setText(comment.getText());

        return commentsRepository.save(existingComment);
    }

    @Override
    public void deleteComment(Long postId, Long id) {
        ensurePostExists(postId);

        Comment comment = commentsRepository.findByIdAndPostId(id, postId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        commentsRepository.delete(comment);

        postsRepository.findById(postId).ifPresent(post -> {
            long current = post.getCommentsCount() != null ? post.getCommentsCount() : 0L;
            if (current > 0) {
                post.setCommentsCount(current - 1);
                postsRepository.save(post);
            }
        });
    }

    private void ensurePostExists(Long postId) {
        if (!postsRepository.existsById(postId)) {
            throw new IllegalArgumentException("Post not found: id=" + postId);
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}