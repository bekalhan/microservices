package com.kalhan.post_service.repository;

import com.kalhan.post_service.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Integer> {
    List<Post> findByUserId(String id);
}
