package com.B0cka.service;

import com.B0cka.dto.FrontPostsRequest;
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
class PostServiceIntegrationTest {

    @Autowired
    private PostService postService;

    @Test
    @DisplayName("Сохранение и получение поста из реальной БД")
    void createAndGetPost() {

        FrontPostsRequest req = new FrontPostsRequest("Real DB Post", "Content", List.of("#wow"));
        Post created = postService.createPost(req);

        assertNotNull(created.getId());
        assertEquals("Real DB Post", created.getTitle());

        Post fromDb = postService.getById(created.getId());

        assertEquals(created.getId(), fromDb.getId());
        assertEquals("Content", fromDb.getText());
    }

    @Test
    @DisplayName("Лайки реально увеличиваются в базе")
    void incrementLikes() {

        FrontPostsRequest req = new FrontPostsRequest("Like Me", "Please", List.of("#wow"));
        Post post = postService.createPost(req);
        Long id = post.getId();

        Long likes = postService.incrementLikes(id);
        assertEquals(1L, likes);

        Post fromDb = postService.getById(id);
        assertEquals(1L, fromDb.getLikesCount());
    }

    @Test
    @DisplayName("Ошибка при поиске несуществующего поста")
    void getById_NotFound() {
        assertThrows(IllegalArgumentException.class, () -> postService.getById(9999L));
    }
}