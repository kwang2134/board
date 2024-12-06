package com.kwang.board.photo.adapters.controller;

import com.kwang.board.global.exception.exceptions.photo.FileUploadException;
import com.kwang.board.photo.application.service.PhotoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoApiController {

    private final PhotoService photoService;

    @PostMapping("/temp-upload")
    public ResponseEntity<String> tempUpload(@RequestParam("file") MultipartFile file,
                                             HttpSession session) {
        // 파일 크기 검증 (4MB)
        if (file.getSize() > 4 * 1024 * 1024L) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body("File size exceeds maximum limit of 4MB");
        }

        try {
            String imagePath = photoService.tempUploadPhoto(file, session.getId());
            return ResponseEntity.ok(imagePath);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid file format");
        } catch (FileUploadException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed");
        }
    }
}
