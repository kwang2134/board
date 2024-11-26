package com.kwang.board.post.application.dto;

import lombok.Data;

@Data
public class PostUpdateDTO {

    private String title;
    private String content;
    private String postType;
}
