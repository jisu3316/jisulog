package com.jisulog.api.repository;

import com.jisulog.api.domain.Post;
import com.jisulog.api.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);
}
