package com.jisulog.api.controller;

import com.jisulog.api.domain.Post;
import com.jisulog.api.domain.PostEditor;
import com.jisulog.api.exception.InvalidRequest;
import com.jisulog.api.request.PostCreate;
import com.jisulog.api.request.PostEdit;
import com.jisulog.api.request.PostSearch;
import com.jisulog.api.response.PostResponse;
import com.jisulog.api.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // SSR -> jsp, thymeleaf, mustache, freemarker
    // -> html rendering
    // SPA -> vue, react
    //  vue -> vue+SSR = nuxt
    // react -> react+SSR =next
    // -> javascript + <-> API (JSNO)

    //Http Method
    //GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD, TRACE, CONNECT
    // 글 등록
    // POST Method

//    @PostMapping("/posts")
//    public String get(@RequestParam String title, @RequestParam String content) {
//       log.info("title={}, content={} ", title, content);
//        return "Hello World";
//    }

//    @PostMapping("/posts")
//    public String get(@RequestParam Map<String, String> params) {
//        log.info("params={}", params);
//        return "Hello World";
//    }

    @PostMapping("/posts")
    public void post(@RequestBody @Valid PostCreate request) {
        //데이터 검증하는 이유

        // 1. client 개발자가 깜빡할 수 있다. 실수로 값을 안보낼 수 있다.
        // 2. client bug로 값이 누락될 수 있다.
        // 3. 외부의 나쁜 사람이 값을 임의로 조좍해서 보낼 수 있따.
        // 4. DB에 값을 저장할 때 의도치 않은 오류가 발생할 수 있다.
        // 5. 서버 개발자의 편암한을 위해서
//        if (bindingResult.hasErrors()) {
//            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
//            FieldError firstFieldError = fieldErrors.get(0);
//            String fieldName = firstFieldError.getField();
//            String errorMessage = firstFieldError.getDefaultMessage();
//
//            Map<String, String> error = new HashMap<>();
//            error.put(fieldName, errorMessage);
//            return error;
//        }
        // Case1. 저장한 데이터 Entity -> response로 응답하기
        // Case2. 저장한 데이터의 primary_id -> response 응답하기
        // Case3. 응답 필요 없음 -> 클라이언트에서 모든 POST(글) 데이터 context를 잘 관리함
        // Bad Case: 서버에서 -> 반드시 이렇게 할 껍니다! fix하면 안됨
        //      -> 서버에서 차라리 유연하게 대응하는게 좋다.
        //      -> 한 번에 일관적으로 잘 처리되는 케이스가 없다. -> 잘 관리하는 형태가 중요하다.

//        if (request.getTitle().contains("바보")) {
//            throw new InvalidRequest();
//        }  이렇게 데이터를 꺼내 와가지고 조합하고 검증하는것은 가능하면 안하는게 좋다. 가능하면 메세지를 던지는 연습을 하면 좋다.
        request.validate();
        postService.write(request);


//        return Map.of();
    }

    /**
     * /posts -> 글 전체 조회(검색 + 페이징)
     * /posts/{postId} -> 글 한개만 조회
     */
    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable(name = "postId") Long postId) {
        return postService.get(postId);

    }

    @GetMapping("/posts")
    public List<PostResponse> getList(@ModelAttribute PostSearch postSearch) {
        return postService.getList(postSearch);
    }


    @PatchMapping("/posts/{postId}")
    public void edit(@PathVariable Long postId, @RequestBody @Valid PostEdit request) {

        postService.edit(postId, request);
    }

    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId) {

        postService.delete(postId);
    }
}
