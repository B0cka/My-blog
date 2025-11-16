package com.B0cka.repository;

import com.B0cka.dto.FrontPostsRequest;
import com.B0cka.model.Posts;
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
    public Posts save(Posts posts) {
        log.info("Сохранение в бд");

        jdbcTemplate.update("insert into posts(title, text, tags) values(?, ?, ?)", Statement.RETURN_GENERATED_KEYS,
               posts.getTitle(), posts.getText(), posts.getTags());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into posts(title, text, tags) values(?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, posts.getTitle());
            ps.setString(2, posts.getText());
            ps.setObject(3, posts.getAuthorId());
            ps.setInt(4, post.getLikesCount() != null ? post.getLikesCount() : 0);
            ps.setBytes(5, post.getImage());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            post.setId(keyHolder.getKey().longValue());
        }

        posts.setId(keyHolder.getKey().longValue());

        return posts;
    }

}
