package com.jisulog.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jisulog.api.domain.Post;
import com.jisulog.api.repository.PostRepository;
import com.jisulog.api.request.PostCreate;
import com.jisulog.api.request.PostEdit;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
class PostControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }


    @Test
    @DisplayName("/posts 요청시 Hello World를 출력한다.")
    void test() throws Exception {
        //글 제목
        //글 내용
        //사용자 데이터
        //id
        //username
        //level
        /**
         * {
         *      "title": "xxx",
         *      "name" : "xxx"
         * }
         */

        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("Hello World")
                .build();

        String json = objectMapper.writeValueAsString(request);
        System.out.println("json = " + json);
        //expected
        // mock 객체는 콘텐츠타입을 application/json 형태로 보낸다. 이거아님
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("/posts 요청시 title값은 필수이다.")
    void test2() throws Exception {
        //given
        PostCreate request = PostCreate.builder()
                .content("내용입니다.")
                .build();
        String json = objectMapper.writeValueAsString(request);
        //expected
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.validation.title").value("타이틀을 입력해주세요."))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("/posts 요청시 DB에 값이 저장된다.")
    void test3() throws Exception {
        //given
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        //then
        assertThat(postRepository.count()).isEqualTo(1);

        Post post = postRepository.findAll().get(0);
        assertThat("제목입니다.").isEqualTo(post.getTitle());
        assertThat("내용입니다.").isEqualTo(post.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void test4() throws Exception {
        //given
        Post post = Post.builder()
                .title("123456789012345")
                .content("bar")
                .build();
        postRepository.save(post);
        //expected
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/{postId}", post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(post.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("1234567890"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value("bar"))
                .andDo(MockMvcResultHandlers.print());


        //then
    }

    @Test
    @DisplayName("글 여러개 조회")
    void test5() throws Exception {
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

        //expected
        mockMvc.perform(MockMvcRequestBuilders.get("/posts?page=1&size=10")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()", Matchers.is(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].title").value("지수 제목 19"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].content").value("지수 내용 19"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("페이지를 0으로 요청하면 첫 페이지를 가져온다.")
    void test6() throws Exception {
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

        //expected
        mockMvc.perform(MockMvcRequestBuilders.get("/posts?page=0&size=10")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()", Matchers.is(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].title").value("지수 제목 19"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].content").value("지수 내용 19"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("글 제목 수정.")
    void test7() throws Exception {
        //given
        Post post = Post.builder()
                .title("지수 제목 ")
                .content("지수 내용 ")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("지수")
                .content("지수 내용 ")
                .build();


        //expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/posts/{postId}", post.getId()) //PATCH /posts/{postId}
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("글 제목 삭제.")
    void test8() throws Exception {
        //given
        Post post = Post.builder()
                .title("지수 제목 ")
                .content("지수 내용 ")
                .build();
        postRepository.save(post);

        //expected
        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/{postId}", post.getId()) //PATCH /posts/{postId}
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회")
    void test9() throws Exception {
        //expected
        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회")
    void test10() throws Exception {
        //given
        PostEdit postEdit = PostEdit.builder()
                .title("지수")
                .content("지수 내용 ")
                .build();

        //expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/posts/{postId}", 1L) //PATCH /posts/{postId}
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("게시글 작성시 제목에 바보는 포함 될 수 없다.")
    void test11() throws Exception {
        //given
        PostCreate request = PostCreate.builder()
                .title("나는 바보입니다.")
                .content("내용입니다.")
                .build();
        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());

        //then
    }
}


// API 문서 생성

// GET /posts/{postId} -> 단건 조회
// POST / posts -> 게시글 등록

//클라이언트 입장 어떤 API 있는지 모름

//Spring RestDocs
// - 운영코드에 -> 영향을 안준다.
// - 코드 수정  -> 문서를 수정 X -> 코드(가능) <-> 문서
// - Test케이스 실행 -> 문서를 생성해준다.