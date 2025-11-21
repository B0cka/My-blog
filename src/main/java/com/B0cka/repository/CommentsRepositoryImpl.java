package com.B0cka.repository;

import com.B0cka.dto.CommentRequestDto;
import com.B0cka.model.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CommentsRepositoryImpl implements CommentsRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Comment> findByPostId(Long postId) {
        return jdbcTemplate.query(
                "SELECT id, post_id, text FROM comments WHERE post_id = ?",
                (rs, rowNum) -> new Comment(
                        rs.getLong("id"),
                        rs.getLong("post_id"),
                        rs.getString("text")
                ),
                postId
        );
    }

    @Override
    public Optional<Comment> findById(Long postId, Long id) {
        var list = jdbcTemplate.query(
                "SELECT id, post_id, text FROM comments WHERE post_id = ? AND id = ?",
                (rs, rowNum) -> new Comment(
                        rs.getLong("id"),
                        rs.getLong("post_id"),
                        rs.getString("text")
                ),
                postId, id
        );
        return list.stream().findFirst();
    }

    @Override
    public Comment save(CommentRequestDto comment) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO comments(post_id, text) VALUES(?, ?)", new String[]{"id"});
            ps.setLong(1, comment.getPostId());
            ps.setString(2, comment.getText());
            return ps;
        }, kh);

        Comment com = Comment.builder()
                .id(kh.getKey().longValue())
                .text(comment.getText())
                .postId(comment.getPostId())
                .build();

        log.info("Сохранение коментария: {}",com);
        return com;
    }

    @Override
    public Comment update(Comment c) {
        jdbcTemplate.update("UPDATE comments SET text=? WHERE id=? AND post_id=?",
                c.getText(), c.getId(), c.getPostId());
        return c;
    }

    @Override
    public void delete(Long postId, Long id) {
        jdbcTemplate.update("DELETE FROM comments WHERE id=? AND post_id=?", id, postId);
    }
}