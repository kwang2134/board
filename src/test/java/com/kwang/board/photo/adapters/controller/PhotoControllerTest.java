package com.kwang.board.photo.adapters.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.util.Random;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class PhotoControllerTest {

    @Autowired
    MockMvc mockMvc;
    
    MockHttpSession httpSession;

    static final String TEST_SESSION_ID = "test-session-id";
    static final String TEST_FILE_NAME = "test-image.jpg";
    static final String TEMP_FILE_PREFIX = "temp_";

    @BeforeEach
    void setUp() {
        httpSession = new MockHttpSession();
        httpSession.setAttribute("sessionId", TEST_SESSION_ID);
    }

    @Test
    @DisplayName("임시 이미지 업로드 테스트")
    void testTempUpload() throws Exception {
        // given
        // Mock 파일 생성
        MockMultipartFile file = new MockMultipartFile(
                "file",
                TEST_FILE_NAME,
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        // when & then
        // 요청 수행
        mockMvc.perform(multipart("/api/photos/temp-upload")
                        .file(file)
                        .with(csrf())
                        .session(httpSession)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/images/" + TEMP_FILE_PREFIX)))
                .andExpect(content().string(containsString(".jpg")));
    }

    @Test
    @DisplayName("파일이 없는 경우 업로드 실패 테스트")
    void testTempUploadWithoutFile() throws Exception {
        // when & then
        mockMvc.perform(multipart("/api/photos/temp-upload")
                        .with(csrf())
                        .session(httpSession))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("잘못된 파일 확장자 업로드 테스트")
    void testTempUploadWithInvalidExtension() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-file",  // 확장자 없음
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "test content".getBytes()
        );

        // when & then
        mockMvc.perform(multipart("/api/photos/temp-upload")
                        .file(file)
                        .with(csrf())
                        .session(httpSession))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("큰 파일 업로드 테스트")
    void testTempUploadWithLargeFile() throws Exception {
        // given
        // 4MB를 초과하는 파일 생성
        byte[] content = new byte[4 * 1024 * 1024 + 1];
        new Random().nextBytes(content);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                TEST_FILE_NAME,
                MediaType.IMAGE_JPEG_VALUE,
                content
        );

        // when & then
        mockMvc.perform(multipart("/api/photos/temp-upload")
                        .file(file)
                        .with(csrf())
                        .session(httpSession))
                .andExpect(status().isPayloadTooLarge());
    }

    @AfterEach
    void tearDown() {
        // 테스트 후 임시 파일 정리
        File directory = new File("D:\\project\\images\\");
        File[] tempFiles = directory.listFiles((dir, name) ->
                name.startsWith(TEMP_FILE_PREFIX) && name.endsWith(".jpg"));
        if (tempFiles != null) {
            for (File file : tempFiles) {
                file.delete();
            }
        }
    }

}