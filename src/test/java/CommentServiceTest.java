
import com.B0cka.dto.CommentRequestDto;
import com.B0cka.model.Comment;
import com.B0cka.model.Post;
import com.B0cka.repository.CommentsRepository;
import com.B0cka.repository.PostsRepository;
import com.B0cka.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService unit tests")
class CommentServiceTest {

    @Mock
    private CommentsRepository commentsRepository;

    @Mock
    private PostsRepository postsRepository;

    @InjectMocks
    private CommentService commentService;

    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(1L)
                .title("Test post")
                .text("Body")
                .tags(List.of("java"))
                .likesCount(0L)
                .commentsCount(0L)
                .build();

        comment = Comment.builder()
                .id(10L)
                .postId(1L)
                .text("Test comment")
                .build();
    }

    @Test
    @DisplayName("createComment: успех при валидных данных")
    void createComment_ok() {
        CommentRequestDto dto = new CommentRequestDto();
        dto.setPostId(1L);
        dto.setText("New comment");

        when(postsRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentsRepository.save(dto)).thenReturn(
                Comment.builder().id(100L).postId(1L).text("New comment").build()
        );

        Comment result = commentService.createComment(1L, dto);

        assertNotNull(result);
        assertEquals(1L, result.getPostId());
        assertEquals("New comment", result.getText());
        verify(commentsRepository, times(1)).save(dto);
    }

    @Test
    @DisplayName("createComment: бросает, если пост не найден")
    void createComment_postNotFound() {
        CommentRequestDto dto = new CommentRequestDto();
        dto.setPostId(1L);
        dto.setText("comment");

        when(postsRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> commentService.createComment(1L, dto));
        verify(commentsRepository, never()).save(any());
    }

    @Test
    @DisplayName("createComment: бросает, если текст пустой")
    void createComment_blankText() {
        CommentRequestDto dto = new CommentRequestDto();
        dto.setPostId(1L);
        dto.setText("   ");

        when(postsRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThrows(IllegalArgumentException.class,
                () -> commentService.createComment(1L, dto));
    }

    @Nested
    @DisplayName("finding comments")
    class Finding {

        @Test
        @DisplayName("findById: успешное получение")
        void findById_ok() {
            when(postsRepository.findById(1L)).thenReturn(Optional.of(post));
            when(commentsRepository.findById(1L, 10L)).thenReturn(Optional.of(comment));

            Comment result = commentService.findById(1L, 10L);

            assertEquals(10L, result.getId());
            assertEquals(1L, result.getPostId());
        }

        @Test
        @DisplayName("findById: падает, если пост не найден")
        void findById_postMissing() {
            when(postsRepository.findById(1L)).thenReturn(Optional.empty());
            assertThrows(IllegalArgumentException.class,
                    () -> commentService.findById(1L, 10L));
        }

        @Test
        @DisplayName("findByPostId: возвращается список")
        void findByPostId_ok() {
            when(postsRepository.findById(1L)).thenReturn(Optional.of(post));
            when(commentsRepository.findByPostId(1L)).thenReturn(List.of(comment));

            List<Comment> result = commentService.findByPostId(1L);

            assertEquals(1, result.size());
            verify(commentsRepository).findByPostId(1L);
        }
    }

    @Test
    @DisplayName("updateComment: меняет текст")
    void updateComment_ok() {
        Comment toUpdate = Comment.builder()
                .id(10L)
                .postId(1L)
                .text("Updated")
                .build();

        when(postsRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentsRepository.findById(1L, 10L)).thenReturn(Optional.of(comment));
        when(commentsRepository.update(any(Comment.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Comment result = commentService.updateComment(1L, toUpdate);

        assertEquals("Updated", result.getText());
        verify(commentsRepository).update(any(Comment.class));
    }

    @Test
    @DisplayName("updateComment: бросает, если комментарий не найден")
    void updateComment_notFound() {
        when(postsRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentsRepository.findById(1L, 10L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(1L, comment));
    }

    @Test
    @DisplayName("deleteComment: успешное удаление")
    void deleteComment_ok() {
        when(postsRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentsRepository.findById(1L, 10L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L, 10L);

        verify(commentsRepository).delete(1L, 10L);
    }

    @Test
    @DisplayName("deleteComment: бросает, если комментарий не найден")
    void deleteComment_notFound() {
        when(postsRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentsRepository.findById(1L, 10L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> commentService.deleteComment(1L, 10L));
    }
}