package com.kwang.board.photo.application.dto;

import lombok.Data;

@Data
public class PhotoDTO {
    private Long id;
    private String photoName;
    private String fileUrl;
    private String uploadAt;
}
