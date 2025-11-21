package com.B0cka.repository;

import com.B0cka.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class PostsRepositoryImpl implements PostsRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Post save(Post post) {
        log.info("Сохранение в бд");

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into posts(title, text, tags, image, likes_count, comments_count) values (?, ?, ?, ?, 0, 0)",
                    new String[]{"id"}
            );
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getText());
            ps.setArray(3, connection.createArrayOf("text", post.getTags().toArray()));
            ps.setBytes(4, post.getImage());
            return ps;
        }, keyHolder);

        post.setId(keyHolder.getKey().longValue());
        post.setLikesCount(0L);
        post.setCommentsCount(0L);
        log.info("Saved post: {}", post);
        return post;
    }

    @Override
    public void updateImg(byte[] bytes, Long id){
        jdbcTemplate.update(
                "UPDATE posts SET image = ? WHERE id = ?",
                bytes, id
        );
    }

    @Override
    public byte[] getPostImage(Long id) {
        return jdbcTemplate.queryForObject(
                "SELECT image FROM posts WHERE id = ?",
                (rs, rowNum) -> rs.getBytes("image"),
                id
        );
    }
    @Override
    public Post update(Post post) {
        log.info("Updating post id={} in DB", post.getId());
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(
                    "UPDATE posts SET title=?, text=?, tags=?, image=?, likes_count=?, comments_count=? WHERE id=?");
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getText());
            ps.setArray(3, connection.createArrayOf("text", post.getTags().toArray()));
            ps.setBytes(4, post.getImage());
            ps.setLong(5, post.getLikesCount());
            ps.setLong(6, post.getCommentsCount());
            ps.setLong(7, post.getId());
            return ps;
        });

        var updated = jdbcTemplate.queryForObject(
                "SELECT id, title, text, tags, likes_count, comments_count, image FROM posts WHERE id=?",
                (rs, rowNum) -> Post.builder()
                        .id(rs.getLong("id"))
                        .title(rs.getString("title"))
                        .text(rs.getString("text"))
                        .tags(java.util.Arrays.asList((String[]) rs.getArray("tags").getArray()))
                        .likesCount(rs.getLong("likes_count"))
                        .commentsCount(rs.getLong("comments_count"))
                        .image(rs.getBytes("image"))
                        .build(),
                post.getId()
        );

        return updated;

    }

    @Override
    public List<Post> findAll() {
        return jdbcTemplate.query("SELECT id, title, text, tags, likes_count, comments_count, image FROM posts", (rs, rowNum) -> Post.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .text(rs.getString("text"))
                .tags(Arrays.asList((String[]) rs.getArray("tags").getArray()))
                .likesCount(rs.getLong("likes_count"))
                .commentsCount(rs.getLong("comments_count"))
                .image(rs.getBytes("image"))
                .build());
    }

    @Override
    public Optional<Post> findById(Long id) {
        List<Post> posts = jdbcTemplate.query("SELECT id, title, text, tags, likes_count, comments_count, image FROM posts WHERE id = ?", (rs, rowNum) -> Post.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .text(rs.getString("text"))
                .tags(Arrays.asList((String[]) rs.getArray("tags").getArray()))
                .likesCount(rs.getLong("likes_count"))
                .commentsCount(rs.getLong("comments_count"))
                .image(rs.getBytes("image"))
                .build(), id);
        return posts.isEmpty() ? Optional.empty() : Optional.of(posts.get(0));
    }

    @Override
    public Long incrementLikes(Long id) {
        jdbcTemplate.update("UPDATE posts SET likes_count = likes_count + 1 WHERE id = ?", id);
        return jdbcTemplate.queryForObject("SELECT likes_count FROM posts WHERE id = ?", Long.class, id);
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update( "DELETE FROM posts WHERE id = ?", id);
    }

}