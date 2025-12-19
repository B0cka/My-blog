package com.B0cka.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("comments")
public class Comment {
    @Id
    private Long id;
    private Long postId;
    private String text;
}
