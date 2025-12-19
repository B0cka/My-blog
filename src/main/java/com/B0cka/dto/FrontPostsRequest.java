package com.B0cka.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
public class FrontPostsRequest {

    @NonNull
    @NotBlank
    private String title;
    @NonNull
    @NotBlank
    private String text;
    @NotEmpty(message = "Tags must not be empty")
    private List<String> tags;

    public FrontPostsRequest(@NonNull String title, @NonNull String text, @NonNull List<String> tags) {
        this.title = title;
        this.text = text;
        this.tags = tags;
    }
}
