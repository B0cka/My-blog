package com.B0cka.service;

import com.B0cka.dto.CommentRequestDto;
import com.B0cka.model.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> findByPostId(Long postId);

    Comment findById(Long postId, Long id);

    Comment createComment(Long postId, CommentRequestDto commentDto);

    Comment updateComment(Long postId, Comment comment);

    void deleteComment(Long postId, Long id);

}
