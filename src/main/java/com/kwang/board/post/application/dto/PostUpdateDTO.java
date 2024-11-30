package com.kwang.board.post.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostUpdateDTO {

    private String title;
    private String content;
    private String postType;
}
