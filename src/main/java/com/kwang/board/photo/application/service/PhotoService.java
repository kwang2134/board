package com.kwang.board.photo.application.service;

import com.kwang.board.photo.domain.model.Photo;
import com.kwang.board.photo.domain.repository.PhotoRepository;
import com.kwang.board.photo.usecase.PhotoCrudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Service
@RequiredArgsConstructor
public class PhotoService implements PhotoCrudUseCase {

    private final PhotoRepository repository;

    @Override
    public Photo uploadPhoto(List<MultipartFile> photos, Long postId) {
        return null;
    }

    @Override
    public void deletePhoto(Long photoId) {

    }

    @Override
    public List<Photo> viewPhotos(Long postId) {
        return List.of();
    }
}
