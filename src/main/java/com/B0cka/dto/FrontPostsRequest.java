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

}
