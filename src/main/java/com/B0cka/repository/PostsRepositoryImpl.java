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

@Repository
@Slf4j
@RequiredArgsConstructor
public class PostsRepositoryImpl implements PostsRepository{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Post save(Post post) {
        log.info("Сохранение в бд");

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into posts(title, text, tags, likes_count, comments_count) values (?, ?, ?, ?, ?)",
                    new String[]{"id"}
            );
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getText());
            ps.setArray(3, connection.createArrayOf("text", post.getTags().toArray()));
            ps.setLong(4, 0);
            ps.setLong(5, 0);
            return ps;
        }, keyHolder);

        post.setId(keyHolder.getKey().longValue());
        post.setLikesCount(0L);
        post.setCommentsCount(0L);

        return post;
    }

}
