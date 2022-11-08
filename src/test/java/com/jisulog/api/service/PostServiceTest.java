package com.jisulog.api.service;

import com.jisulog.api.domain.Post;
import com.jisulog.api.exception.PostNotFound;
import com.jisulog.api.repository.PostRepository;
import com.jisulog.api.request.PostCreate;
import com.jisulog.api.request.PostEdit;
import com.jisulog.api.request.PostSearch;
import com.jisulog.api.response.PostResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }
    @Test
    @DisplayName("글 작성")
    void test1() {
        //given
        PostCreate postCreate = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        //when
        postService.write(postCreate);


        //then
        assertThat(postRepository.count()).isEqualTo(1);
        Post post = postRepository.findAll().get(0);
        assertThat(post.getTitle()).isEqualTo("제목입니다.");
        assertThat(post.getContent()).isEqualTo("내용입니다.");
    }

    @Test
    @DisplayName("글 1개 조회")
    void test2() {
        //given
        Post requestPost = Post.builder()
                .title("123456789012345")
                .content("내용입니다.")
                .build();
        postRepository.save(requestPost);


        //when
        PostResponse response = postService.get(requestPost.getId());

        //then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("1234567890");
        assertThat(response.getContent()).isEqualTo("내용입니다.");
    }

    @Test
    @DisplayName("글 1페이지  조회")
    void test3() {
        //given
        List<Post> requestPosts = IntStream.range(0, 20)
                .mapToObj(i -> {
                    return Post.builder()
                            .title("지수 제목 " + i)
                            .content("지수 내용 " + i)
                            .build();
                })
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                .page(1)
                .build();

        //when
        List<PostResponse> posts = postService.getList(postSearch);

        //then

        assertThat(10).isEqualTo(posts.size());
        assertThat("지수 제목 19").isEqualTo(posts.get(0).getTitle());
    }

    @Test
    @DisplayName("글 제목 수정")
    void test4() {
        //given
        Post post = Post.builder()
                            .title("지수 제목 " )
                            .content("지수 내용 ")
                            .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("지수")
                .content("지수 내용 ")
                .build();

        //when
        postService.edit(post.getId(), postEdit);

        //then
        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id=" + post.getId()));
        assertThat(changedPost.getTitle()).isEqualTo("지수"); //실패 사유 : 뒤에것을 원했지만 앞에것이다. 성공 사유 : 뒤에것을 원했는데 앞에것이다.

    }

    @Test
    @DisplayName("글 내용 수정")
    void test5() {
        //given
        Post post = Post.builder()
                .title("지수 제목 " )
                .content("지수 내용 ")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("지수")
                .content("지수 내용2 ")
                .build();

        //when
        postService.edit(post.getId(), postEdit);

        //then
        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id=" + post.getId()));
        assertThat(changedPost.getContent()).isEqualTo("지수 내용2 "); //실패 사유 : 뒤에것을 원했지만 앞에것이다. 성공 사유 : 뒤에것을 원했는데 앞에것이다.
    }

    @Test
    @DisplayName("게시글 삭제")
    void test6() {
        //given
        Post post = Post.builder()
                .title("지수 제목 " )
                .content("지수 내용 ")
                .build();
        postRepository.save(post);

        //when
        postService.delete(post.getId());

        //then
        assertThat(postRepository.count()).isEqualTo(0); //실패 사유 : 뒤에것을 원했지만 앞에것이다. 성공 사유 : 뒤에것을 원했는데 앞에것이다.
    }

    @Test
    @DisplayName("글 1개 조회 - 존재하지 않는 글")
    void test7() {
        //given
        Post post = Post.builder()
                .title("지수 제목 " )
                .content("지수 내용 ")
                .build();
        postRepository.save(post);



        //expected
        assertThrows(PostNotFound.class, () -> {
            postService.get(post.getId() + 1L);
        });

    }

    @Test
    @DisplayName("게시글 삭제 - 존재하지 않는 글")
    void test8() {
        //given
        Post post = Post.builder()
                .title("지수 제목 " )
                .content("지수 내용 ")
                .build();
        postRepository.save(post);

        //expected
        assertThrows(PostNotFound.class, () -> {
            postService.delete(post.getId() + 1L);
        });
    }

    @Test
    @DisplayName("글 내용 수정 - 존재하지 않는 글")
    void test9() {
        //given
        Post post = Post.builder()
                .title("지수 제목 " )
                .content("지수 내용 ")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("지수")
                .content("지수 내용2 ")
                .build();

        //expected
        assertThrows(PostNotFound.class, () -> {
            postService.edit(post.getId() + 1L, postEdit);
        });
    }
}