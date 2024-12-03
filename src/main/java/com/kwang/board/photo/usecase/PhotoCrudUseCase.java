package com.kwang.board.photo.usecase;

import com.kwang.board.photo.domain.model.Photo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PhotoCrudUseCase {
    String tempUploadPhoto(MultipartFile file, String sessionId);

    List<Photo> uploadPhoto(Long postId, String sessionId);

    List<Photo> updatePhoto(Long postId, String sessionId);

    void deletePhoto(Long postId);

    List<Photo> viewPhotos(Long postId);

    void cleanupTempFiles();
}
