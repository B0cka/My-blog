package com.B0cka.model;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    private Long id;
    private Long postId;
    private String text;
}
