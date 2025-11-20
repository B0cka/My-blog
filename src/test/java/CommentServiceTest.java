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


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testing CommentService in isolation")
class CommentServiceTest {

    @Mock
    private CommentsRepository commentsRepo;
    @Mock
    private PostsRepository postsRepo;
    @InjectMocks
    private CommentService service;

    private Post samplePost;
    private Comment sampleComment;

    @BeforeEach
    void prepareData() {
        samplePost = Post.builder()
                .id(10L)
                .title("My Post")
                .text("Some text for checking")
                .likesCount(0L).commentsCount(0L)
                .tags(List.of("unit", "mockito"))
                .build();

        sampleComment = Comment.builder()
                .id(5L)
                .postId(10L)
                .text("initial comment")
                .build();
    }

    @Test
    @DisplayName("Create comment: happy path")
    void createShouldPersistComment() {
        when(postsRepo.findById(10L)).thenReturn(Optional.of(samplePost));
        when(commentsRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Comment result = service.createComment(10L, sampleComment);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("initial comment", result.getText()),
                () -> verify(commentsRepo, atLeastOnce()).save(any())
        );
    }

    @Test
    @DisplayName("Create comment: throws if post absent")
    void createShouldFailIfPostMissing() {
        when(postsRepo.findById(anyLong())).thenReturn(Optional.empty());
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> service.createComment(99L, sampleComment));
        assertTrue(ex.getMessage().toLowerCase().contains("not found"));
    }

    @Test
    @DisplayName("Create comment: throws for blank text")
    void createShouldRejectEmptyText() {
        when(postsRepo.findById(10L)).thenReturn(Optional.of(samplePost));
        sampleComment.setText("   ");
        assertThrows(IllegalArgumentException.class,
                () -> service.createComment(10L, sampleComment));
    }

    @Nested
    @DisplayName("Finding comments")
    class FindingBlock {

        @Test
        @DisplayName("Fetch by id: ok")
        void getExistingComment() {
            when(postsRepo.findById(10L)).thenReturn(Optional.of(samplePost));
            when(commentsRepo.findById(10L, 5L)).thenReturn(Optional.of(sampleComment));

            Comment found = service.findById(10L, 5L);
            assertEquals(5L, found.getId());
        }

        @Test
        @DisplayName("Fetch by id: unknown post")
        void getThrowsIfPostNotExist() {
            when(postsRepo.findById(10L)).thenReturn(Optional.empty());
            assertThrows(IllegalArgumentException.class, () -> service.findById(10L, 5L));
        }

        @Test
        @DisplayName("Fetch by post id: returns list")
        void getListOfComments() {
            when(postsRepo.findById(10L)).thenReturn(Optional.of(samplePost));
            when(commentsRepo.findByPostId(10L)).thenReturn(Collections.singletonList(sampleComment));

            List<Comment> list = service.findByPostId(10L);

            assertEquals(1, list.size());
            verify(commentsRepo).findByPostId(10L);
        }
    }

    @Test
    @DisplayName("Update comment: text changed")
    void updateShouldModifyText() {
        Comment toUpdate = Comment.builder()
                .id(5L).postId(10L).text("modified").build();

        when(postsRepo.findById(10L)).thenReturn(Optional.of(samplePost));
        when(commentsRepo.findById(10L, 5L)).thenReturn(Optional.of(sampleComment));
        when(commentsRepo.update(any())).thenAnswer(inv -> inv.getArgument(0));

        Comment updated = service.updateComment(10L, toUpdate);
        assertEquals("modified", updated.getText());
        verify(commentsRepo).update(any());
    }

    @Test
    @DisplayName("Update comment: fails when missing id")
    void updateShouldFailIfCommentAbsent() {
        when(postsRepo.findById(10L)).thenReturn(Optional.of(samplePost));
        when(commentsRepo.findById(10L, 5L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> service.updateComment(10L, sampleComment));
    }

    @Test
    @DisplayName("Delete comment successfully")
    void deletionScenario() {
        when(postsRepo.findById(10L)).thenReturn(Optional.of(samplePost));
        when(commentsRepo.findById(10L, 5L)).thenReturn(Optional.of(sampleComment));

        service.deleteComment(10L, 5L);

        verify(commentsRepo, times(1)).delete(10L, 5L);
    }

    @Test
    @DisplayName("Delete: comment not found -> exception")
    void deletionMissingComment() {
        when(postsRepo.findById(10L)).thenReturn(Optional.of(samplePost));
        when(commentsRepo.findById(10L, 5L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.deleteComment(10L, 5L));
    }
}