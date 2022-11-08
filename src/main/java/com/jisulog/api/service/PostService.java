package com.jisulog.api.service;

import com.jisulog.api.domain.Post;
import com.jisulog.api.domain.PostEditor;
import com.jisulog.api.exception.PostNotFound;
import com.jisulog.api.repository.PostRepository;
import com.jisulog.api.request.PostCreate;
import com.jisulog.api.request.PostEdit;
import com.jisulog.api.request.PostSearch;
import com.jisulog.api.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    public void write(PostCreate postCreate) {
        Post post = Post.builder()
                .title(postCreate.getTitle())
                .content(postCreate.getContent())
                .build();
        postRepository.save(post);
    }

    public PostResponse get(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }

    //글이 너무 많은 경우 - > 비용이 너무 많이 든다.
    // 글이 1000000000 - > 1억개 있을경우 DB에서 모두 조회하는 경우 -> DB가 뻗을 수 있다.
    // DB -> 애플리케이션 서버로 전달하는 시간, 트래픽 비용 등이 많이 발생할 수 있다.

    public List<PostResponse> getList(PostSearch postSearch) {
//        Pageable pageable = PageRequest.of(page, 5, Sort.by("id").descending());
//        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC,"id"));
       return postRepository.getList(postSearch).stream()
               .map(post -> new PostResponse(post))
               .collect(Collectors.toList());
    }

    @Transactional
    public void edit(Long id, PostEdit postEdit) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);

        PostEditor.PostEditorBuilder postEditorBuilder = post.toEditor();

//        if (postEdit.getTitle() != null) {
//            postEditorBuilder.title(postEdit.getTitle());
//        }
//
//        if (postEdit.getContent() != null) {
//            postEditorBuilder.content(postEdit.getContent());
//        }

        PostEditor postEditor = postEditorBuilder.title(postEdit.getTitle())
                .content(postEdit.getContent())
                .build();

        post.edit(postEditor);


    }

    public void delete(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);
       //존재하는경우
        postRepository.delete(post);
    }
}
