package com.kwang.board.photo.adapters.controller;

import com.kwang.board.photo.application.service.PhotoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping("/temp-upload")
    public ResponseEntity<String> tempUpload(@RequestParam("file") MultipartFile file,
                                             HttpSession session) {
        String imagePath = photoService.tempUploadPhoto(file, session.getId());
        return ResponseEntity.ok(imagePath);
    }
}
