package com.kwang.board.photo.domain.repository;

import com.kwang.board.photo.domain.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByPostId(Long postId);
}
