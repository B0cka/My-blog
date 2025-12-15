CREATE TABLE IF NOT EXISTS posts (
    id BIGSERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    text TEXT NOT NULL,
    tags TEXT[],
    likes_count BIGINT DEFAULT 0,
    comments_count BIGINT DEFAULT 0,
    image BYTEA
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT REFERENCES posts(id) ON DELETE CASCADE,
    text TEXT NOT NULL
);