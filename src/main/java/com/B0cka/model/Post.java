package com.B0cka.model;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {

    private Long id;
    private String title;
    private String text;
    private List<String> tags;
    private Long likesCount;
    private Long commentsCount;

}
