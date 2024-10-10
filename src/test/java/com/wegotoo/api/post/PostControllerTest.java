package com.wegotoo.api.post;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wegotoo.api.ControllerTestSupport;
import com.wegotoo.api.post.request.ContentEditRequest;
import com.wegotoo.api.post.request.ContentWriteRequest;
import com.wegotoo.api.post.request.PostEditRequest;
import com.wegotoo.api.post.request.PostWriteRequest;
import com.wegotoo.domain.post.ContentType;
import com.wegotoo.support.security.WithAuthUser;
import java.util.List;
import java.util.stream.IntStream;
import lombok.With;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

class PostControllerTest extends ControllerTestSupport {

    @Test
    @WithAuthUser
    @DisplayName("유저가 게시글 생성 API를 호출한다.")
    void writePost() throws Exception {
        // given
        List<ContentWriteRequest> contents = getContentWriteRequest(0, 4, "내용");
        PostWriteRequest request = getPostWriteRequest(contents);
        // when // then
        mockMvc.perform(post("/v1/posts")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @WithAuthUser
    @DisplayName("유저가 게시글 생성 API를 호출 할 때에는 제목은 필수이다.")
    void writePostWithTitle() throws Exception {
        // given
        List<ContentWriteRequest> contents = getContentWriteRequest(0, 4, "내용");
        PostWriteRequest request = PostWriteRequest.builder()
                .contents(contents)
                .build();
        // when // then
        mockMvc.perform(post("/v1/posts")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("제목은 필수입니다."));
    }

    @Test
    @WithAuthUser
    @DisplayName("유저가 게시글 생성 API를 호출 할 때에는 타입은 필수이다.")
    void writePostWithType() throws Exception {
        // given
        List<ContentWriteRequest> contents = IntStream.range(0, 1)
                .mapToObj(i -> ContentWriteRequest.builder()
                        .text("내용")
                        .build()).toList();
        PostWriteRequest request = getPostWriteRequest(contents);
        // when // then
        mockMvc.perform(post("/v1/posts")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("타입은 필수입니다."));
    }

    @Test
    @WithAuthUser
    @DisplayName("유저가 게시글 생성 API를 호출 할 때에는 TEXT는 필수이다.")
    void writePostWithText() throws Exception {
        // given
        List<ContentWriteRequest> contents = IntStream.range(0, 1)
                .mapToObj(i -> ContentWriteRequest.builder()
                        .type(ContentType.T)
                        .build()).toList();
        PostWriteRequest request = getPostWriteRequest(contents);
        // when // then
        mockMvc.perform(post("/v1/posts")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("TEXT는 필수입니다."));
    }

    @Test
    @WithAuthUser
    @DisplayName("좋아요한 게시글 조회 API를 호출한다.")
    void findLikePosts() throws Exception {
        // when // then
        mockMvc.perform(get("/v1/users/likes/posts")
                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @WithAuthUser
    @DisplayName("유저가 게시글을 수정하는 API를 호출한다.")
    void editPost() throws Exception {
        // given
        ContentEditRequest content = ContentEditRequest.builder()
                .id(0L)
                .type(ContentType.T)
                .text("첫 문단 수정")
                .build();

        PostEditRequest request = PostEditRequest.builder()
                .title("제목 수정")
                .contents(List.of(content))
                .build();
        // when // then
        mockMvc.perform(patch("/v1/posts/{postId}", 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    private static PostWriteRequest getPostWriteRequest(List<ContentWriteRequest> contents) {
        return PostWriteRequest.builder()
                .title("제목")
                .contents(contents)
                .build();
    }

    private List<ContentWriteRequest> getContentWriteRequest(int start, int end, String text) {
        return IntStream.range(start, end)
                .mapToObj(i -> ContentWriteRequest.builder()
                        .type(ContentType.T)
                        .text(text + i)
                        .build()).toList();
    }

}