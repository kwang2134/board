package com.kwang.board;

import com.kwang.board.comment.domain.repository.CommentRepository;
import com.kwang.board.post.domain.model.Post;
import com.kwang.board.post.domain.model.PostType;
import com.kwang.board.post.domain.repository.PostRepository;
import com.kwang.board.user.domain.model.User;
import com.kwang.board.user.domain.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TestInit {
    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.initUser();
        initService.initPost();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final UserRepository userRepository;
        private final PostRepository postRepository;
        private final CommentRepository commentRepository;
        private final PasswordEncoder encoder;

        private Long userId;


        public void initUser() {
            User user = User.builder()
                    .loginId("kwang2134")
                    .password(encoder.encode("1234"))
                    .username("kwangh")
                    .email("kwang2134@naver.com")
                    .build();

            User savedUser = userRepository.save(user);
            savedUser.changeManager();
            userId = savedUser.getId();
        }

        public void initPost() {
            User user = userRepository.findById(userId).get();

            for (int i = 1; i <= 100; i++) {
                Post newPost = Post.builder()
                        .title("일반글 " + i + " 제목입니다.")
                        .content("일반글 " + i + " 내용입니다.")
                        .postType(PostType.NORMAL)
                        .displayName("kwang2134")
                        .user(user)
                        .build();

                Post noticePost = Post.builder()
                        .title("공지글 " + i + " 제목입니다.")
                        .content("공지글 " + i + " 내용입니다.")
                        .postType(PostType.NOTICE)
                        .displayName("kwang2134")
                        .user(user)
                        .build();

                Post popularPost = Post.builder()
                        .title("인기글 " + i + " 제목입니다.")
                        .content("인기글 " + i + " 내용입니다.")
                        .postType(PostType.POPULAR)
                        .displayName("Anonymous")
                        .password("1111")
                        .build();

                postRepository.save(newPost);
                postRepository.save(noticePost);
                postRepository.save(popularPost);
            }
        }
    }

}
