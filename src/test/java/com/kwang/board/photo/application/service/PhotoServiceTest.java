package com.kwang.board.photo.application.service;

import com.kwang.board.global.exception.exceptions.photo.FileUploadException;
import com.kwang.board.global.exception.exceptions.post.PostNotFoundException;
import com.kwang.board.photo.domain.model.Photo;
import com.kwang.board.photo.domain.repository.PhotoRepository;
import com.kwang.board.post.domain.model.Post;
import com.kwang.board.post.domain.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {

    public static final String SESSION_ID = "user";
    @Mock
    private PhotoRepository photoRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private PhotoService service;

    private static final String TEST_UPLOAD_PATH = "src/test/resources/test-uploads/";

    @BeforeEach
    void setUp() {
        // 테스트용 업로드 디렉토리 생성
        new File(TEST_UPLOAD_PATH).mkdirs();
        // UPLOAD_PATH 값을 테스트용 경로로 변경
        ReflectionTestUtils.setField(service, "UPLOAD_PATH", TEST_UPLOAD_PATH);

    }

    @AfterEach
    void cleanup() {
        // 테스트 후 생성된 파일들 삭제
        FileSystemUtils.deleteRecursively(new File(TEST_UPLOAD_PATH));
    }

    @Test
    void tempUploadPhoto_Success() throws IOException {
        // given
        String originalFileName = "test.jpg";
        when(multipartFile.getOriginalFilename()).thenReturn(originalFileName);

        // transferTo 메서드 동작 정의 추가
        doAnswer(invocation -> {
            File file = invocation.getArgument(0);
            new FileOutputStream(file).close();
            return null;
        }).when(multipartFile).transferTo(any(File.class));

        // when
        String tempFilePath = service.tempUploadPhoto(multipartFile, SESSION_ID);

        // then
        assertThat(tempFilePath).startsWith("/images/temp_");
        assertThat(tempFilePath).endsWith(".jpg");

        // 임시 파일이 실제로 생성되었는지 확인
        File tempFile = new File(TEST_UPLOAD_PATH + tempFilePath.substring(8));
        assertThat(tempFile).exists();
    }

    @Test
    void tempUploadPhoto_InvalidFileName() {
        // given
        when(multipartFile.getOriginalFilename()).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> service.tempUploadPhoto(multipartFile, SESSION_ID))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tempUploadPhoto_IOExceptionCase() throws IOException {
        // given
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        doThrow(new IOException()).when(multipartFile).transferTo(any(File.class));

        // when & then
        assertThatThrownBy(() -> service.tempUploadPhoto(multipartFile, SESSION_ID))
                .isInstanceOf(FileUploadException.class);
    }


    @Test
    void uploadPhoto_Success() throws IOException {
        // given
        String originalFileName = "test.jpg";
        when(multipartFile.getOriginalFilename()).thenReturn(originalFileName);

        doAnswer(invocation -> {
            File file = invocation.getArgument(0);
            new FileOutputStream(file).close();
            return null;
        }).when(multipartFile).transferTo(any(File.class));

        //임시 파일 생성
        String tempFilePath = service.tempUploadPhoto(multipartFile, SESSION_ID);
        File tempFile = new File(tempFilePath);

        //임시 파일 주소로 post 생성
        Long postId = 1L;
        String con = "게시글 본문 내용 중 이미지가 포함되는 형식<img src='" + tempFilePath + "'>이미지가 가운데 포함되어 있음";
        Post post = Post.builder()
                .id(postId)
                .content(con)
                .build();


        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(photoRepository.save(any(Photo.class))).thenAnswer(i -> i.getArgument(0));

        // when
        List<Photo> photos = service.uploadPhoto(List.of(multipartFile), postId , SESSION_ID);

        // then
        // 1. 임시 파일이 정식 파일로 변환되었는지 확인
        assertThat(tempFile).doesNotExist();
        assertThat(photos).hasSize(1);

        // 2. 본문의 이미지 경로가 업데이트되었는지 확인
        assertThat(post.getContent()).doesNotContain(tempFilePath.substring(8));
        assertThat(post.getContent()).contains(photos.get(0).getSavedPhotoName());

        // 3. tempFileMap에서 해당 postId 정보가 삭제되었는지 확인
        Map<String, Set<String>> tempFileMap = (Map<String, Set<String>>) ReflectionTestUtils.getField(service, "tempFileMap");
        assertThat(tempFileMap).doesNotContainKey(SESSION_ID);

        // 4. 직접 확인
        log.info("content = {}", con);
        log.info("post.getContent = {}", post.getContent());
    }

    @Test
    void uploadPhoto_PostNotFound() {
        // given
        when(postRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                service.uploadPhoto(List.of(multipartFile), 1L, SESSION_ID))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void uploadPhoto_NoTempFiles() {
        // given
        Post post = Post.builder().id(1L).content("content").build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // when
        List<Photo> photos = service.uploadPhoto(List.of(multipartFile), 1L, SESSION_ID);

        // then
        assertThat(photos).isEmpty();
    }

    @Test
    void viewPhotos_Success() {
        // given
        Long postId = 1L;
        List<Photo> photoList = List.of(Photo.builder().build(), Photo.builder().build());
        when(photoRepository.findByPostId(postId)).thenReturn(photoList);

        // when
        List<Photo> result = service.viewPhotos(postId);

        // then
        assertThat(result).hasSize(2);
        verify(photoRepository).findByPostId(postId);
    }

    @Test
    void viewPhotos_EmptyList() {
        // given
        when(photoRepository.findByPostId(any())).thenReturn(Collections.emptyList());

        // when
        List<Photo> result = service.viewPhotos(1L);

        // then
        assertThat(result).isEmpty();
    }
}