package com.kwang.board.post.adapters.controller;

import com.kwang.board.comment.adapters.mapper.CommentMapper;
import com.kwang.board.comment.application.dto.CommentDTO;
import com.kwang.board.comment.application.service.CommentService;
import com.kwang.board.comment.domain.model.Comment;
import com.kwang.board.global.exception.exceptions.UnauthorizedAccessException;
import com.kwang.board.post.adapters.mapper.PostMapper;
import com.kwang.board.post.application.dto.PostDTO;
import com.kwang.board.post.application.dto.PostSearchCond;
import com.kwang.board.post.application.service.PostService;
import com.kwang.board.post.domain.model.Post;
import com.kwang.board.post.domain.model.PostType;
import com.kwang.board.user.adapters.security.userdetails.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class PostFormController {

    private final PostService postService;
    private final CommentService commentService;

    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

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
        model.addAttribute("pageGroup", pageGroup);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasNext", posts.hasNext());
        model.addAttribute("hasPrev", posts.hasPrevious());
        model.addAttribute("searchCond", new PostSearchCond());

        return "posts/list-form";
    }

    // 탭 전환 시 게시글 목록 프래그먼트 반환
    @GetMapping("posts/tab")
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
        model.addAttribute("pageGroup", pageGroup);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasNext", posts.hasNext());
        model.addAttribute("hasPrev", posts.hasPrevious());
        model.addAttribute("searchCond", new PostSearchCond());

        return "posts/list-form :: #posts-fragment";
    }

    // 페이지 전환 시 게시글 목록 프래그먼트 반환
    @GetMapping("posts/page")
    public String getPostsByPage(@RequestParam PostType type,
                                 @RequestParam(required = false) Integer pageGroup,
                                 @PageableDefault(size = 10) Pageable pageable, Model model) {
        Page<Post> posts = switch (type) {
            case NORMAL -> postService.viewNormalPosts(pageable);
            case NOTICE -> postService.viewNoticePosts(pageable);
            case POPULAR -> postService.viewPopularPosts(pageable);
        };

        int currentPage = posts.getNumber() + 1;
        int totalPages = posts.getTotalPages();

        // pageGroup이 null이면 현재 페이지로 계산
        if (pageGroup == null) {
            pageGroup = (currentPage - 1) / 9;
        }

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

        return "posts/list-form :: #posts-fragment";
    }

    // 게시글 검색 시 검색된 게시글 프래그먼트로 반환
    @GetMapping("posts/search")
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

        return "posts/list-form :: #posts-fragment";
    }

    // 검색 페이지 전환
    @GetMapping("posts/search/page")
    public String getSearchPostsByPage(@RequestParam String searchType,
                                       @RequestParam String keyword,
                                       @RequestParam(required = false) Integer pageGroup,
                                       @PageableDefault(size = 10) Pageable pageable,
                                       Model model) {
        // 검색 조건 생성
        PostSearchCond searchCond = createSearchCond(searchType, keyword);
        Page<Post> posts = postService.searchPosts(searchCond, pageable);


        // 페이지네이션 정보 계산
        int currentPage = posts.getNumber() + 1;
        int totalPages = posts.getTotalPages();

        // pageGroup이 null이면 현재 페이지로 계산
        if (pageGroup == null) {
            pageGroup = (currentPage - 1) / 9;
        }

        int startPage = pageGroup * 9 + 1;
        int endPage = Math.min(startPage + 8, totalPages);


        // 모델에 데이터 추가
        model.addAttribute("posts", postMapper.toDTOList(posts.getContent()));
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("pageGroup", pageGroup);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasNext", posts.hasNext());
        model.addAttribute("hasPrev", posts.hasPrevious());

        // 검색 조건 유지를 위한 데이터
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchCond", searchCond);

        return "posts/list-form :: #posts-fragment";
    }

    /**
     * 게시글 작성 실패 시 다시 작성 폼으로 redirect
     * session 에 저장된 작성 내용을 가져옴
     * session 에 작성된 내용이 없는 경우 새로운 게시글 작성
     */

    @GetMapping("post/write")
    public String createPost(@AuthenticationPrincipal CustomUserDetails userDetails, Model model, HttpSession session) {
        PostDTO.Request request = (PostDTO.Request) session.getAttribute("postCreateRequest");

        if (request == null) {
            request = new PostDTO.Request();
            if (userDetails != null) {
                request.setDisplayName(userDetails.getDisplayName());
            }
        }

        if (request.getType() == null) {
            request.setType("NORMAL");
        }

        model.addAttribute("request", request);
        return "posts/write-form";
    }

    // 게시글 조회
    @GetMapping("post/{id}")
    public String viewPost(@AuthenticationPrincipal CustomUserDetails userDetails,
                           @PathVariable("id") Long postId,
                           @PageableDefault(size = 15) Pageable pageable, Model model) {
        // 게시글 조회
        Post post = postService.viewPost(postId);

        // 댓글 작성용 객체
        CommentDTO.Request commentRequest = new CommentDTO.Request();
        if (userDetails != null) {
            // 로그인 상태면 작성자명 미리 설정
            commentRequest.setDisplayName(userDetails.getDisplayName());
        }

        // 해당 게시글의 댓글 조회
        Page<Comment> comments = commentService.viewComts(postId, pageable);

        // 페이지네이션 계산
        int currentPage = comments.getNumber() + 1;
        int totalPages = comments.getTotalPages();
        int pageGroup = (currentPage - 1) / 9;
        int startPage = pageGroup * 9 + 1;
        int endPage = Math.min(startPage + 8, totalPages);

        // 게시글 정보
        model.addAttribute("post", postMapper.toResponseDTO(post));
        model.addAttribute("commentCount", comments.getTotalElements());
        // 회원 게시글 여부
        model.addAttribute("user", post.getUser() != null ? post.getUser().getId() : "");

        // 댓글 관련 데이터
        model.addAttribute("comments", commentMapper.toResponseListDTO(comments.getContent()));
        model.addAttribute("commentRequest", commentRequest);  // 작성용

        // 페이지네이션 정보
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasNext", comments.hasNext());
        model.addAttribute("hasPrev", comments.hasPrevious());

        return "posts/view-form";
    }

    /**
     * 게시글 수정 실패 시 다시 수정 폼으로 redirect
     * session 에 저장된 작성 내용을 가져옴
     * session 에 작성된 내용이 없는 경우 새로운 게시글 수정 요청으로 취급
     */
    @GetMapping("post/{id}/edit")
    public String updatePost(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable("id") Long postId,
                             @RequestParam(required = false) String password,
                             Model model,
                             HttpSession session) {

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

        PostDTO.Request request = (PostDTO.Request) session.getAttribute("postEditRequest");

        if (request == null) {
            request = postMapper.toRequestDTO(post);
        }

        if (request.getType() == null) {
            request.setType("NORMAL");
        }

        model.addAttribute("request", request);
        model.addAttribute("postId", post.getId());
        return "posts/edit-form";

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
