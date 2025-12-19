package com.B0cka.service;

import com.B0cka.dto.CommentRequestDto;
import com.B0cka.dto.FrontPostsRequest;
import com.B0cka.model.Comment;
import com.B0cka.model.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Test
    @DisplayName("Полный цикл: Создание поста -> Комментария -> Проверка счетчика")
    void createCommentFlow() {
        Post post = postService.createPost(new FrontPostsRequest("Post Title", "Content", List.of()));
        Long postId = post.getId();

        CommentRequestDto commentReq = new CommentRequestDto();
        commentReq.setText("My Comment");

        Comment createdComment = commentService.createComment(postId, commentReq);

        assertNotNull(createdComment.getId());
        assertEquals("My Comment", createdComment.getText());
        assertEquals(postId, createdComment.getPostId());

        Post updatedPost = postService.getById(postId);
        assertEquals(1L, updatedPost.getCommentsCount());
    }

    @Test
    @DisplayName("Удаление комментария уменьшает счетчик")
    void deleteCommentFlow() {
        Post post = postService.createPost(new FrontPostsRequest("Title", "Txt", List.of("#wow")));
        CommentRequestDto req = new CommentRequestDto();
        req.setText("C1");
        Comment c = commentService.createComment(post.getId(), req);

        commentService.deleteComment(post.getId(), c.getId());

        assertThrows(IllegalArgumentException.class, () ->
                commentService.findById(post.getId(), c.getId())
        );

        Post updatedPost = postService.getById(post.getId());
        assertEquals(0L, updatedPost.getCommentsCount());
    }
}