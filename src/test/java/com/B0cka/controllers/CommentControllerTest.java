package com.B0cka.controllers;

import com.B0cka.dto.CommentRequestDto;
import com.B0cka.model.Comment;
import com.B0cka.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentsController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @Test
    @DisplayName("POST /api/posts/{id}/comments -> 201 Created")
    void createComment_shouldReturnCreated() throws Exception {
        Long postId = 1L;
        CommentRequestDto req = new CommentRequestDto();
        req.setText("New Comment");

        Comment created = Comment.builder()
                .id(10L)
                .postId(postId)
                .text("New Comment")
                .build();

        when(commentService.createComment(eq(postId), any(CommentRequestDto.class)))
                .thenReturn(created);

        mockMvc.perform(post("/api/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.text").value("New Comment"))
                .andExpect(jsonPath("$.postId").value(1L));
    }

    @Test
    @DisplayName("GET /api/posts/{id}/comments -> 200 OK (List)")
    void getComments_shouldReturnList() throws Exception {
        Long postId = 1L;
        List<Comment> comments = List.of(
                Comment.builder().id(1L).text("First").build(),
                Comment.builder().id(2L).text("Second").build()
        );

        when(commentService.findByPostId(postId)).thenReturn(comments);

        mockMvc.perform(get("/api/posts/{postId}/comments", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // Проверяем размер списка
                .andExpect(jsonPath("$[0].text").value("First"));
    }

    @Test
    @DisplayName("PUT /api/posts/{postId}/comments/{id} -> 200 OK")
    void updateComment_shouldReturnUpdated() throws Exception {
        Long postId = 1L;
        Long commentId = 5L;

        Comment updateInfo = Comment.builder().text("Updated Text").build();

        Comment updatedComment = Comment.builder()
                .id(commentId)
                .postId(postId)
                .text("Updated Text")
                .build();

        when(commentService.updateComment(eq(postId), any(Comment.class)))
                .thenReturn(updatedComment);

        mockMvc.perform(put("/api/posts/{postId}/comments/{id}", postId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Updated Text"));
    }

    @Test
    @DisplayName("DELETE /api/posts/{postId}/comments/{id} -> 200 OK")
    void deleteComment_shouldReturnOk() throws Exception {
        Long postId = 1L;
        Long commentId = 5L;

        mockMvc.perform(delete("/api/posts/{postId}/comments/{id}", postId, commentId))
                .andExpect(status().isOk());

        verify(commentService).deleteComment(postId, commentId);
    }
}