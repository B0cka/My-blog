package com.B0cka.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
public class FrontPostsRequest {

    @NonNull //<- проверка на не нулевые значения
    private String title;
    @NonNull
    private String text;
    @NonNull
    private List<String> tags;

    public FrontPostsRequest(@NonNull String title, @NonNull String text, @NonNull List<String> tags) {
        this.title = title;
        this.text = text;
        this.tags = tags;
    }
}
