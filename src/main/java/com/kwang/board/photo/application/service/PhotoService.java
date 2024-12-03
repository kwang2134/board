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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PhotoService implements PhotoCrudUseCase {

    private final PhotoRepository repository;
    private final PostRepository postRepository;

//    private static final String UPLOAD_PATH = "D:\\project\\images\\";
    private String UPLOAD_PATH = "D:\\project\\images\\";
    private static final String TEMP_PREFIX = "temp_";

    private final Map<String, Set<String>> tempFileMap = new ConcurrentHashMap<>();

    /**
     * 실행 순서
     * 이미지 추가 요청 -> tempUploadPhoto 실행 -> 본문에 이미지 경로 반환 ->
     * 게시글 등록 요청 -> createPost 실행 -> uploadPhoto 실행
     */

    @Override
    public String tempUploadPhoto(MultipartFile file, String sessionId) {
        String originalFilename = file.getOriginalFilename();
        String tempFileName = TEMP_PREFIX + UUID.randomUUID() + getExtension(originalFilename);

        try {
            File savedFile = new File(UPLOAD_PATH + tempFileName);
            file.transferTo(savedFile);
            // 임시 파일명과 원본 파일명 매핑 저장
            tempFileMap.computeIfAbsent(sessionId, k -> new HashSet<>()).add(tempFileName);
            return "/images/" + tempFileName;
        } catch (IOException e) {
            throw new FileUploadException("Failed to upload temp file: " + originalFilename);
        }
    }

    @Override
    @Transactional
    public List<Photo> uploadPhoto(Long postId, String sessionId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        List<Photo> savedPhotos = new ArrayList<>();
        Map<String, String> pathMap = new HashMap<>();
        String content = post.getContent();

        // 본문에서 임시 파일명들 추출
        Set<String> tempFileNames = tempFileMap.get(sessionId);
        if (tempFileNames != null) {
            for (String tempFileName : tempFileNames) {
                if (content.contains(tempFileName)) {
                    String newFileName = String.format("%d_%s%s",
                            postId, UUID.randomUUID(), getExtension(tempFileName));

                    try {
                        // 1. 임시 파일을 정식 파일로 이동
                        File tempFile = new File(UPLOAD_PATH + tempFileName);
                        File newFile = new File(UPLOAD_PATH + newFileName);
                        if (tempFile.exists()) {
                            tempFile.renameTo(newFile);
                        }

                        // 2. Photo 엔티티 생성 및 저장
                        Photo photo = Photo.builder()
                                .post(post)
                                .originalPhotoName(tempFileName)
                                .savedPhotoName(newFileName)
                                .photoPath("/images/" + newFileName)
                                .build();

                        savedPhotos.add(repository.save(photo));

                        // 3. 경로 매핑 저장
                        pathMap.put("/images/" + tempFileName, "/images/" + newFileName);

                    } catch (Exception e) {
                        throw new FileUploadException("Failed to process file: " + tempFileName);
                    }
                }
            }
        }

        // 4. 본문의 이미지 경로 업데이트
        for (Map.Entry<String, String> entry : pathMap.entrySet()) {
            content = content.replace(entry.getKey(), entry.getValue());
        }
        post.updateContent(content);

        // 5. 처리된 임시 파일 정보 삭제
        tempFileMap.remove(sessionId);

        return savedPhotos;
    }

    @Override
    @Transactional
    public List<Photo> updatePhoto(Long postId, String sessionId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        List<Photo> savedPhotos = new ArrayList<>();
        Map<String, String> pathMap = new HashMap<>();
        String content = post.getContent();

        // 1. 기존 사진들 중 본문에서 제거된 사진 삭제
        List<Photo> existingPhotos = repository.findByPostId(postId);
        for (Photo photo : existingPhotos) {
            if (!content.contains(photo.getPhotoPath())) {
                // 파일 시스템에서 삭제
                File fileToDelete = new File(UPLOAD_PATH + photo.getSavedPhotoName());
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
                // DB에서 삭제
                repository.delete(photo);
            }
        }

        // 2. 새로 추가된 임시 파일 처리
        Set<String> tempFileNames = tempFileMap.get(sessionId);
        if (tempFileNames != null) {
            for (String tempFileName : tempFileNames) {
                if (content.contains(tempFileName)) {
                    String newFileName = String.format("%d_%s%s",
                            postId, UUID.randomUUID(), getExtension(tempFileName));

                    try {
                        // 임시 파일을 정식 파일로 이동
                        File tempFile = new File(UPLOAD_PATH + tempFileName);
                        File newFile = new File(UPLOAD_PATH + newFileName);
                        if (tempFile.exists()) {
                            tempFile.renameTo(newFile);
                        }

                        // Photo 엔티티 생성 및 저장
                        Photo photo = Photo.builder()
                                .post(post)
                                .originalPhotoName(tempFileName)
                                .savedPhotoName(newFileName)
                                .photoPath("/images/" + newFileName)
                                .build();

                        savedPhotos.add(repository.save(photo));
                        pathMap.put("/images/" + tempFileName, "/images/" + newFileName);

                    } catch (Exception e) {
                        throw new FileUploadException("Failed to process file: " + tempFileName);
                    }
                }
            }
        }

        // 3. 본문의 이미지 경로 업데이트
        for (Map.Entry<String, String> entry : pathMap.entrySet()) {
            content = content.replace(entry.getKey(), entry.getValue());
        }
        post.updateContent(content);

        // 4. 처리된 임시 파일 정보 삭제
        tempFileMap.remove(sessionId);

        return savedPhotos;
    }


    private String getExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.lastIndexOf(".") == -1) {
            throw new IllegalArgumentException("Invalid file name: " + originalFilename);
        }
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    @Override
    public void deletePhoto(Long postId) {
        List<Photo> photos = repository.findByPostId(postId);
        for (Photo photo : photos) {
            File file = new File(UPLOAD_PATH + photo.getSavedPhotoName());
            if (file.exists()) {
                file.delete();
            }
        }
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
