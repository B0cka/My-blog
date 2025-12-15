package com.B0cka.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("posts")
public class Post {
    @Id
    private Long id;
    private String title;
    private String text;
    private List<String> tags;
    private Long likesCount;
    private Long commentsCount;
    private byte[] image;
}
