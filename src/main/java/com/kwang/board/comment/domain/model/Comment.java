package com.kwang.board.comment.domain.model;

import com.kwang.board.comment.application.dto.CommentUpdateDTO;
import com.kwang.board.global.domain.BaseEntity;
import com.kwang.board.post.domain.model.Post;
import com.kwang.board.user.domain.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "password")
    private String password;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> childComments = new ArrayList<>();

    public void modify(CommentUpdateDTO dto) {
        this.content = dto.getContent();
    }

    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
