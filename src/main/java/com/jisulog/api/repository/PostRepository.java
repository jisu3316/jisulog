package com.jisulog.api.repository;

import com.jisulog.api.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>,PostRepositoryCustom{
}
