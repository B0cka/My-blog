package com.B0cka.controllers;

import com.B0cka.dto.CommentRequestDto;
import com.B0cka.model.Comment;
import com.B0cka.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Slf4j
public class CommentsController {

    private final CommentService commentService;

    @GetMapping("/{postId}/comments")
    public List<Comment> getComments(@PathVariable("postId") Long postId) {
        return commentService.findByPostId(postId);
    }

    @GetMapping("/{postId}/comments/{id}")
    public Comment getComment(@PathVariable("postId") Long postId,
                              @PathVariable("id") Long id) {
        return commentService.findById(postId, id);
    }

    @PostMapping("/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public Comment addComment(@PathVariable("postId") Long postId,
                              @RequestBody CommentRequestDto comment) {
        return commentService.createComment(postId, comment);
    }

    @PutMapping("/{postId}/comments/{id}")
    public Comment updateComment(@PathVariable("postId") Long postId,
                                 @PathVariable("id") Long id,
                                 @RequestBody Comment comment) {
        comment.setId(id);
        return commentService.updateComment(postId, comment);
    }

    @DeleteMapping("/{postId}/comments/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteComment(@PathVariable("postId") Long postId,
                              @PathVariable("id") Long id) {
        commentService.deleteComment(postId, id);
    }
}