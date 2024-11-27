package com.kwang.board.post.domain.model;

import com.kwang.board.comment.domain.model.Comment;
import com.kwang.board.global.domain.BaseEntity;
import com.kwang.board.photo.domain.model.Photo;
import com.kwang.board.post.application.dto.PostUpdateDTO;
import com.kwang.board.user.domain.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "password")
    private String password;

    @Column(name = "recom_count", columnDefinition = "INT DEFAULT 0")
    private int recomCount;

    @Column(name = "not_recom_count", columnDefinition = "INT DEFAULT 0")
    private int notRecomCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false)
    private PostType postType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos = new ArrayList<>();

    public void modify(PostUpdateDTO dto){
        this.title = dto.getTitle();
        this.content = dto.getContent();
        if (dto.getPostType() != null) {
            this.postType = PostType.valueOf(dto.getPostType());
        }
    }

    public int recommendPost() {
        this.recomCount++;

        return recomCount;
    }

    public int notRecommendPost() {
        this.notRecomCount++;

        return notRecomCount;
    }

    public void changeTypeNormal() {
        this.postType = PostType.NORMAL;
    }

    public void changeTypeNotice() {
        this.postType = PostType.NOTICE;
    }

    public void changeTypePopular() {
        this.postType = PostType.POPULAR;
    }
}
