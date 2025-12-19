package com.B0cka.controllers;

import com.B0cka.dto.FrontPostsRequest;
import com.B0cka.dto.PostsResponse;
import com.B0cka.model.Post;
import com.B0cka.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostsController.class)
public class PostsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/posts -> 201 Created")
    void createPost_shouldReturnCreated() throws Exception {

        FrontPostsRequest req = new FrontPostsRequest("Test Title", "Text", List.of("tag"));

        Post createdPost = Post.builder()
                .id(1L)
                .title("Test Title")
                .text("Text")
                .likesCount(0L)
                .build();

        when(postService.createPost(any(FrontPostsRequest.class))).thenReturn(createdPost);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Title"));
    }

    @Test
    @DisplayName("GET /api/posts/{id} -> 200 OK")
    void getPost_shouldReturnPost() throws Exception {
        Post post = Post.builder().id(10L).title("Title").build();
        when(postService.getById(10L)).thenReturn(post);

        mockMvc.perform(get("/api/posts/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    @DisplayName("PUT /api/posts/{id}/image -> 200 OK")
    void uploadImage_shouldReturnOk() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image", "hello.jpg", MediaType.IMAGE_JPEG_VALUE, "Bytes".getBytes()
        );

        mockMvc.perform(multipart("/api/posts/1/image")
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk());

        verify(postService).savePostImage(eq(1L), any());
    }

    @Test
    @DisplayName("GET /api/posts (Pagination) -> 200 OK")
    void getPosts_shouldReturnPagedResponse() throws Exception {
        PostsResponse resp = new PostsResponse(List.of(), false, false, 1);
        when(postService.getPagedPosts(any(), anyInt(), anyInt())).thenReturn(resp);

        mockMvc.perform(get("/api/posts")
                        .param("pageNumber", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastPage").value(1));
    }


    private int anyInt() {
        return any(Integer.class);
    }

}