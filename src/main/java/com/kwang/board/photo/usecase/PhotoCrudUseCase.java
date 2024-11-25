package com.kwang.board.photo.usecase;

import com.kwang.board.photo.domain.model.Photo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PhotoCrudUseCase {
    Photo uploadPhoto(List<MultipartFile> photos, Long postId);

    void deletePhoto(Long photoId);

//    List<PhotoDTO> viewPhotos(Long postId);
}
