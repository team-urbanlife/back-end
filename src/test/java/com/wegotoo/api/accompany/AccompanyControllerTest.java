package com.wegotoo.api.accompany;

import static com.wegotoo.support.security.MockAuthUtils.authorizationHeaderName;
import static com.wegotoo.support.security.MockAuthUtils.mockBearerToken;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wegotoo.api.ControllerTestSupport;
import com.wegotoo.api.accompany.request.AccompanyCreateRequest;
import com.wegotoo.api.accompany.request.AccompanyEditRequest;
import com.wegotoo.support.security.WithAuthUser;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AccompanyControllerTest extends ControllerTestSupport {

    final LocalDate START_DATE = LocalDate.of(2020, 1, 1);
    final LocalDate END_DATE = LocalDate.of(2020, 12, 31);

    @Test
    @WithAuthUser
    @DisplayName("여행 모집글 생성하는 API를 호출한다.")
    void createAccompany() throws Exception {
        // given
        AccompanyCreateRequest request = AccompanyCreateRequest.builder()
                .startDate(START_DATE)
                .endDate(END_DATE)
                .title("제주도 여행 모집")
                .location("제주도")
                .latitude(0.0)
                .longitude(0.0)
                .personnel(3)
                .startAge(20)
                .endAge(29)
                .cost(1000000)
                .content("여행 관련 글")
                .build();

        // when // then
        mockMvc.perform(post("/v1/accompanies")
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
    @DisplayName("여행 모집글 수정하는 API를 호출한다.")
    void editAccompany() throws Exception {
        // given
        AccompanyEditRequest request = AccompanyEditRequest.builder()
                .startDate(START_DATE)
                .endDate(END_DATE)
                .title("제주도 여행 모집")
                .location("제주도")
                .latitude(0.0)
                .longitude(0.0)
                .personnel(3)
                .startAge(20)
                .endAge(29)
                .cost(1000000)
                .content("여행 관련 글")
                .build();

        // when // then
        mockMvc.perform(patch("/v1/accompanies/{accompanyId}", 1)
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
    @DisplayName("여행 모집글 삭제하는 API를 호출한다.")
    void deleteAccompany() throws Exception {
        // when // then
        mockMvc.perform(delete("/v1/accompanies/{accompanyId}", 1)
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("사용자가 모집글을 조회하는 API를 호출한다.")
    void findAccompany() throws Exception {

        // when // then
        mockMvc.perform(get("/v1/accompanies")
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
    @DisplayName("사용자가 작성한 모집글을 조회하는 API를 호출한다.")
    void findAllUserAccompany() throws Exception {
        // when // then
        mockMvc.perform(get("/v1/users/accompanies")
                        .header(authorizationHeaderName(), mockBearerToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("사용자가 모집글을 단건 조회하는 API를 호출한다.")
    void findOneAccompany() throws Exception {

        // when // then
        mockMvc.perform(get("/v1/accompanies/1")
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

}