package com.kwang.board.post.adapters.controller;

import com.kwang.board.global.exception.exceptions.UnauthorizedAccessException;
import com.kwang.board.post.adapters.mapper.PostMapper;
import com.kwang.board.post.application.dto.PostDTO;
import com.kwang.board.post.application.dto.PostSearchCond;
import com.kwang.board.post.application.service.PostService;
import com.kwang.board.post.domain.model.Post;
import com.kwang.board.post.domain.model.PostType;
import com.kwang.board.user.adapters.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class PostFormController {

    private final PostService postService;
    private final PostMapper postMapper;

    // 인덱스 페이지 (일반 게시글 목록)
    @GetMapping
    public String index(Model model, @PageableDefault(size = 10) Pageable pageable) {
        Page<Post> posts = postService.viewNormalPosts(pageable);

        // 현재 페이지 (0부터 시작하므로 1을 더함)
        int currentPage = posts.getNumber() + 1;
        // 전체 페이지 수
        int totalPages = posts.getTotalPages();

        // 현재 페이지가 속한 페이지 그룹의 시작과 끝
        int pageGroup = (currentPage - 1) / 9;
        int startPage = pageGroup * 9 + 1;
        int endPage = Math.min(startPage + 8, totalPages);

        model.addAttribute("posts", postMapper.toDTOList(posts.getContent()));
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasNext", posts.hasNext());
        model.addAttribute("hasPrev", posts.hasPrevious());
        model.addAttribute("searchCond", new PostSearchCond());

        return "posts/list";
    }

    // 탭 전환 시 게시글 목록 프래그먼트 반환
    @GetMapping("/posts/tab")
    public String getPostsByType(@RequestParam PostType type,
                                 @PageableDefault(size = 10) Pageable pageable, Model model) {
        Page<Post> posts = switch (type) {
            case NORMAL -> postService.viewNormalPosts(pageable);
            case NOTICE -> postService.viewNoticePosts(pageable);
            case POPULAR -> postService.viewPopularPosts(pageable);
        };

        int currentPage = posts.getNumber() + 1;
        int totalPages = posts.getTotalPages();
        int pageGroup = (currentPage - 1) / 9;
        int startPage = pageGroup * 9 + 1;
        int endPage = Math.min(startPage + 8, totalPages);

        model.addAttribute("posts", postMapper.toDTOList(posts.getContent()));
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasNext", posts.hasNext());
        model.addAttribute("hasPrev", posts.hasPrevious());
        model.addAttribute("searchCond", new PostSearchCond());

        return "posts/list :: #postsFragment";
    }

    // 페이지 전환 시 게시글 목록 프래그먼트 반환
    @GetMapping("/posts/page")
    public String getPostsByPage(@RequestParam PostType type,
                                 @RequestParam int pageGroup,
                                 @PageableDefault(size = 10) Pageable pageable, Model model) {
        Page<Post> posts = switch (type) {
            case NORMAL -> postService.viewNormalPosts(pageable);
            case NOTICE -> postService.viewNoticePosts(pageable);
            case POPULAR -> postService.viewPopularPosts(pageable);
        };

        int currentPage = posts.getNumber() + 1;
        int totalPages = posts.getTotalPages();
        int startPage = pageGroup * 9 + 1;
        int endPage = Math.min(startPage + 8, totalPages);

        model.addAttribute("posts", postMapper.toDTOList(posts.getContent()));
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasNext", posts.hasNext());
        model.addAttribute("hasPrev", posts.hasPrevious());
        model.addAttribute("searchCond", new PostSearchCond());

        return "posts/list :: #postsFragment";
    }

    // 게시글 검색 시 검색된 게시글 프래그먼트로 반환
    @GetMapping("/posts/search")
    public String searchPosts(@RequestParam String searchType,
                              @RequestParam String keyword,
                              @PageableDefault(size = 10) Pageable pageable,
                              Model model) {
        PostSearchCond searchCond = createSearchCond(searchType, keyword);
        Page<Post> posts = postService.searchPosts(searchCond, pageable);

        int currentPage = posts.getNumber() + 1;
        int totalPages = posts.getTotalPages();
        int pageGroup = (currentPage - 1) / 9;
        int startPage = pageGroup * 9 + 1;
        int endPage = Math.min(startPage + 8, totalPages);

        model.addAttribute("posts", postMapper.toDTOList(posts.getContent()));
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasNext", posts.hasNext());
        model.addAttribute("hasPrev", posts.hasPrevious());

        // 검색 조건 유지를 위한 정보 추가
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchCond", searchCond);

        return "posts/list :: #postsFragment";
    }

    // 검색 페이지 전환
    @GetMapping("/posts/search/page")
    public String getSearchPostsByPage(@RequestParam String searchType,
                                       @RequestParam String keyword,
                                       @RequestParam int pageGroup,
                                       @PageableDefault(size = 10) Pageable pageable,
                                       Model model) {
        // 검색 조건 생성
        PostSearchCond searchCond = createSearchCond(searchType, keyword);
        Page<Post> posts = postService.searchPosts(searchCond, pageable);

        // 페이지네이션 정보 계산
        int currentPage = posts.getNumber() + 1;
        int totalPages = posts.getTotalPages();
        int startPage = pageGroup * 9 + 1;
        int endPage = Math.min(startPage + 8, totalPages);

        // 모델에 데이터 추가
        model.addAttribute("posts", postMapper.toDTOList(posts.getContent()));
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasNext", posts.hasNext());
        model.addAttribute("hasPrev", posts.hasPrevious());

        // 검색 조건 유지를 위한 데이터
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchCond", searchCond);

        return "posts/list :: #postsFragment";
    }

    @GetMapping("/post/write")
    public String createPost(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        PostDTO.Request request = new PostDTO.Request();
        if (userDetails != null) {
            // 로그인 상태면 작성자명 미리 설정
            request.setDisplayName(userDetails.getDisplayName());
        }
        model.addAttribute("request", request);
        return "post/write";
    }

    @GetMapping("/post/{id}/edit")
    public String updatePost(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable("id") Long postId,
                             @RequestParam(required = false) String password,
                             Model model) {

        Post post = postService.viewPost(postId);

        // 회원 게시글인 경우
        if (post.getUser() != null) {
            if (checkPermission(userDetails, post)) {
                throw new UnauthorizedAccessException("게시글에 대한 수정 권한이 없습니다.");
            }
        }

        // 비회원 게시글인 경우
        else {
            if (checkPassword(postId, password)) {
                throw new UnauthorizedAccessException("비밀번호가 일치하지 않습니다.");
            }
        }

        model.addAttribute("request", postMapper.toRequestDTO(post));
        return "post/edit";

    }

    private boolean checkPassword(Long postId, String password) {
        return password == null || !postService.checkNonUserPost(postId, password);
    }

    private boolean checkPermission(CustomUserDetails userDetails, Post post) {
        return userDetails == null || !post.getUser().getId().equals(userDetails.getId());
    }


    private PostSearchCond createSearchCond(String searchType, String keyword) {
        PostSearchCond searchCond = new PostSearchCond();
        switch (searchType) {
            case "TITLE":
                searchCond.setTitle(keyword);
                break;
            case "CONTENT":
                searchCond.setContent(keyword);
                break;
            case "TITLE_CONTENT":
                searchCond.setTitle(keyword);
                searchCond.setContent(keyword);
                break;
            case "AUTHOR":
                searchCond.setAuthor(keyword);
                break;
        }
        return searchCond;
    }
}
