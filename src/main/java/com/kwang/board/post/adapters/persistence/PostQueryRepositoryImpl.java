package com.kwang.board.post.adapters.persistence;

import com.kwang.board.post.application.dto.PostSearchCond;
import com.kwang.board.post.domain.model.Post;
import com.kwang.board.post.domain.repository.PostQueryRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kwang.board.post.domain.model.QPost.post;

@Repository
@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Post> searchPosts(PostSearchCond searchCond, Pageable pageable) {
        // 전체 검색 쿼리
        List<Post> posts = queryFactory
                .selectFrom(post)
                .where(
                        searchCondition(searchCond)
                )
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 쿼리
        Long total = queryFactory
                .select(post.count())
                .from(post)
                .where(
                        searchCondition(searchCond)
                )
                .fetchOne();

        return new PageImpl<>(posts, pageable, total);
    }

    private BooleanExpression searchCondition(PostSearchCond searchCond) {
        if (searchCond.getTitle() != null && searchCond.getContent() != null) {
            return titleContains(searchCond.getTitle())
                    .or(contentContains(searchCond.getContent()));
        } else if (searchCond.getTitle() != null) {
            return titleContains(searchCond.getTitle());
        } else if (searchCond.getContent() != null) {
            return contentContains(searchCond.getContent());
        } else if (searchCond.getAuthor() != null) {
            return authorContains(searchCond.getAuthor());
        }

        return null;
    }

    private BooleanExpression titleContains(String title) {
        return title != null ? post.title.contains(title) : null;
    }

    private BooleanExpression contentContains(String content) {
        return content != null ? post.content.contains(content) : null;
    }

    private BooleanExpression authorContains(String author) {
        return author != null ? post.user.username.contains(author) : null;
    }
}
