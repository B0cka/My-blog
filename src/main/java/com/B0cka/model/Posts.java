package com.B0cka.model;

import lombok.*;

import java.util.ArrayList;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Posts {

    private Long id;
    private String title;
    private String text;
    private ArrayList<String> tags;
    private Long likesCount;
    private Long commentsCount;

}
