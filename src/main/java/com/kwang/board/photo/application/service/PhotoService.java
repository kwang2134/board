package com.kwang.board.photo.application.service;

import com.kwang.board.global.exception.exceptions.photo.FileUploadException;
import com.kwang.board.global.exception.exceptions.post.PostNotFoundException;
import com.kwang.board.photo.domain.model.Photo;
import com.kwang.board.photo.domain.repository.PhotoRepository;
import com.kwang.board.photo.usecase.PhotoCrudUseCase;
import com.kwang.board.post.domain.model.Post;
import com.kwang.board.post.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotoService implements PhotoCrudUseCase {

    private final PhotoRepository repository;
    private final PostRepository postRepository;

    private static final String UPLOAD_PATH = "D:\\project\\images\\";
    private static final String TEMP_PREFIX = "temp_";

    /**
     * 실행 순서
     * 이미지 추가 요청 -> tempUploadPhoto 실행 -> 본문에 이미지 경로 반환 ->
     * 게시글 등록 요청 -> createPost 실행 -> uploadPhoto 실행
     */

    @Override
    @Transactional
    public String tempUploadPhoto(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String tempFileName = TEMP_PREFIX + UUID.randomUUID() + extension;

        try {
            File savedFile = new File(UPLOAD_PATH + tempFileName);
            file.transferTo(savedFile);
            return "/images/" + tempFileName;  // 웹에서 접근 가능한 URL 반환
        } catch (IOException e) {
            throw new FileUploadException("Failed to upload temp file: " + originalFilename);
        }
    }

    @Override
    @Transactional
    public List<Photo> uploadPhoto(List<MultipartFile> photos, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        List<Photo> savedPhotos = new ArrayList<>();

        for (int i = 0; i < photos.size(); i++) {
            MultipartFile file = photos.get(i);
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // temp_ 로 시작하는 임시 파일을 정식 파일로 변환
            String savedFileName = String.format("%d_%d_%s%s",
                    postId, (i + 1), UUID.randomUUID(), extension);

            try {
                // 임시 파일을 정식 파일로 이동
                File tempFile = new File(UPLOAD_PATH + TEMP_PREFIX + originalFilename);
                File newFile = new File(UPLOAD_PATH + savedFileName);
                if (tempFile.exists()) {
                    tempFile.renameTo(newFile);
                } else {
                    file.transferTo(newFile);
                }

                Photo photo = Photo.builder()
                        .post(post)
                        .originalPhotoName(originalFilename)
                        .savedPhotoName(savedFileName)
                        .photoPath("/images/" + savedFileName)  // 웹 접근 경로로 저장
                        .build();

                savedPhotos.add(repository.save(photo));
            } catch (IOException e) {
                throw new FileUploadException("Failed to save file: " + originalFilename);
            }
        }

        return savedPhotos;
    }

    @Override
    @Transactional
    public void deletePhoto(Long photoId) {
        repository.deleteById(photoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Photo> viewPhotos(Long postId) {
        return repository.findByPostId(postId);
    }

    @Override
    @Scheduled(cron = "0 0 1 * * ?")  // 매일 새벽 1시에 실행
    public void cleanupTempFiles() {
        File directory = new File(UPLOAD_PATH);
        File[] tempFiles = directory.listFiles((dir, name) -> name.startsWith(TEMP_PREFIX));
        if (tempFiles != null) {
            for (File file : tempFiles) {
                if (file.lastModified() < System.currentTimeMillis() - 24 * 60 * 60 * 1000) {
                    file.delete();
                }
            }
        }
    }
}
