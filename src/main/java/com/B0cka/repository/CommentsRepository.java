package com.B0cka.repository;

import com.B0cka.dto.CommentRequestDto;
import com.B0cka.model.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentsRepository {
    List<Comment> findByPostId(Long postId);
    Optional<Comment> findById(Long postId, Long id);
    Comment save(CommentRequestDto comment);
    Comment update(Comment comment);
    void delete(Long postId, Long id);
}