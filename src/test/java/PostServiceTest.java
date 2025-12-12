
import com.B0cka.controllers.PostsController;
import com.B0cka.dto.FrontPostsRequest;
import com.B0cka.dto.PostFullDto;
import com.B0cka.dto.PostsResponse;
import com.B0cka.model.Post;
import com.B0cka.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostsController unit tests")
class PostServiceTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostsController controller;

    @Test
    @DisplayName("createPost: возвращает 201 и DTO")
    void createPost_ok() {
        FrontPostsRequest req = new FrontPostsRequest("title", "text", List.of("t1"));
        Post post = Post.builder()
                .id(1L)
                .title("title")
                .text("text")
                .tags(List.of("t1"))
                .likesCount(0L)
                .commentsCount(0L)
                .build();

        when(postService.createPost(req)).thenReturn(post);

        ResponseEntity<PostFullDto> resp = controller.createPost(req);

        assertEquals(201, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertEquals(1L, resp.getBody().getId());
        verify(postService).createPost(req);
    }

    @Test
    @DisplayName("updatePost: возвращает 200 и обновлённый DTO")
    void updatePost_ok() {
        FrontPostsRequest req = new FrontPostsRequest("new", "updated", List.of());
        Post updated = Post.builder()
                .id(2L)
                .title("new")
                .text("updated")
                .likesCount(1L)
                .commentsCount(0L)
                .build();

        when(postService.updatePost(2L, req)).thenReturn(updated);

        ResponseEntity<PostFullDto> resp = controller.updatePost(2L, req);

        assertEquals(200, resp.getStatusCodeValue());
        assertEquals("new", resp.getBody().getTitle());
        verify(postService).updatePost(2L, req);
    }

    @Test
    @DisplayName("addLike: возвращает количество лайков")
    void addLike_ok() {
        when(postService.incrementLikes(3L)).thenReturn(5L);

        Long result = controller.addLike(3L);

        assertEquals(5L, result);
        verify(postService).incrementLikes(3L);
    }

    @Test
    @DisplayName("getPostById: возвращает Post")
    void getPostById_ok() {
        Post post = Post.builder().id(4L).title("one").text("t").build();
        when(postService.getById(4L)).thenReturn(post);

        Post result = controller.getPostById(4L);

        assertEquals(4L, result.getId());
        verify(postService).getById(4L);
    }

    @Test
    @DisplayName("uploadImage: 200 OK")
    void uploadImage_ok() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "img".getBytes()
        );

        ResponseEntity<Void> response = controller.uploadImage(1L, file);

        assertEquals(200, response.getStatusCodeValue());
        verify(postService).savePostImage(1L, file);
    }

    @Test
    @DisplayName("getPostImage: возвращает байты")
    void getPostImage_ok() {
        byte[] img = new byte[]{1, 2, 3};
        when(postService.getPostImage(1L)).thenReturn(img);

        byte[] result = controller.getPostImage(1L);

        assertArrayEquals(img, result);
        verify(postService).getPostImage(1L);
    }

    @Test
    @DisplayName("getPosts: возвращает PostsResponse")
    void getPosts_ok() {
        PostsResponse pr = new PostsResponse(List.of(), false, false, 1);
        when(postService.getPagedPosts("", 1, 5)).thenReturn(pr);

        PostsResponse result = controller.getPosts("", 1, 5);

        assertEquals(1, result.getLastPage());
        verify(postService).getPagedPosts("", 1, 5);
    }
}