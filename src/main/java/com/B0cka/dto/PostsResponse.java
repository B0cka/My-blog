package com.B0cka.dto;

import com.B0cka.model.Post;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PostsResponse {
    private List<Post> posts;
    private boolean hasPrev;
    private boolean hasNext;
    private int lastPage;
}