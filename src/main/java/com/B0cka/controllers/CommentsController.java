package com.B0cka.controllers;

import com.B0cka.model.Comment;
import com.B0cka.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
@Slf4j
public class CommentsController {

    private final CommentService commentService;

    @GetMapping
    public List<Comment> getComments(@PathVariable Long postId) {
        return commentService.findByPostId(postId);
    }

    @GetMapping("/{id}")
    public Comment getComment(@PathVariable Long postId, @PathVariable Long id) {
        return commentService.findById(postId, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Comment addComment(@PathVariable Long postId, @RequestBody Comment comment) {
        return commentService.createComment(postId, comment);
    }

    @PutMapping("/{id}")
    public Comment updateComment(@PathVariable Long postId,
                                 @PathVariable Long id,
                                 @RequestBody Comment comment) {
        comment.setId(id);
        return commentService.updateComment(postId, comment);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteComment(@PathVariable Long postId, @PathVariable Long id) {
        commentService.deleteComment(postId, id);
    }
}