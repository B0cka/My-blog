package com.B0cka.dto;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;

@Data
public class FrontPostsRequest {

    @NonNull //<- проверка на не нулевые значения
    private String title;
    @NonNull
    private String text;
    @NonNull
    private ArrayList<String> tags;

}
