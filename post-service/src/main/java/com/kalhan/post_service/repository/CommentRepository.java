package com.kalhan.post_service.repository;

import com.kalhan.post_service.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
