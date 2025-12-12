package com.B0cka.mapper;

import com.B0cka.dto.PostFullDto;
import com.B0cka.model.Post;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PostMapper {

    public static PostFullDto toDto(Post post){

        return PostFullDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .tags(post.getTags())
                .text(post.getText())
                .commentsCount(post.getCommentsCount())
                .likesCount(post.getLikesCount())
                .build();
    }

}
