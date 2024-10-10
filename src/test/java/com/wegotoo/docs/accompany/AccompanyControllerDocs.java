package com.wegotoo.docs.accompany;

import static com.wegotoo.domain.accompany.Gender.NO_MATTER;
import static com.wegotoo.support.security.MockAuthUtils.authorizationHeaderName;
import static com.wegotoo.support.security.MockAuthUtils.mockBearerToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wegotoo.api.accompany.request.AccompanyCreateRequest;
import com.wegotoo.api.accompany.request.AccompanyEditRequest;
import com.wegotoo.application.OffsetLimit;
import com.wegotoo.application.SliceResponse;
import com.wegotoo.application.accompany.response.AccompanyFindAllResponse;
import com.wegotoo.application.accompany.response.AccompanyFindOneResponse;
import com.wegotoo.docs.RestDocsSupport;
import com.wegotoo.support.security.WithAuthUser;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

public class AccompanyControllerDocs extends RestDocsSupport {

    private final LocalDate START_DATE = LocalDate.of(2024, 9, 1);
    private final LocalDate END_DATE = LocalDate.of(2024, 9, 2);

    @Test
    @WithAuthUser
    @DisplayName("동행 모집 글을 생성하는 API")
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
                .gender("선호 성별")
                .startAge(20)
                .endAge(29)
                .cost(1000000)
                .content("여행 관련 글")
                .build();

        AccompanyFindOneResponse response = AccompanyFindOneResponse.builder()
                .accompanyId(1L)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .title("동행 제목")
                .content("동행 내용")
                .userName("사용자 이름")
                .registeredDateTime(LocalDateTime.of(2024, 9, 1, 0, 0, 0))
                .views(0L)
                .likeCount(0L)
                .location("지역")
                .personnel(4)
                .gender(NO_MATTER)
                .startAge(20)
                .endAge(29)
                .cost(1000000)
                .userProfileImage("이미지 URL")
                .build();

        given(accompanyService.createAccompany(anyLong(), any(), any()))
                .willReturn(response);

        // when // then
        mockMvc.perform(post("/v1/accompanies")
                        .header(authorizationHeaderName(), mockBearerToken())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("accompany/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("어세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("startDate").type(STRING)
                                        .description("여행 시작 일자 (YYYY-MM-DD)"),
                                fieldWithPath("endDate").type(STRING)
                                        .description("여행 종료 일자 (YYYY-MM-DD)"),
                                fieldWithPath("title").type(STRING)
                                        .description("동행 제목"),
                                fieldWithPath("location").type(STRING)
                                        .description("지역 이름"),
                                fieldWithPath("latitude").type(NUMBER)
                                        .description("위도"),
                                fieldWithPath("longitude").type(NUMBER)
                                        .description("경도"),
                                fieldWithPath("personnel").type(NUMBER)
                                        .description("모집 인원"),
                                fieldWithPath("gender").type(STRING)
                                        .description("모집 성별 -> man(남성만), woman(여성만), null(상관없음)"),
                                fieldWithPath("startAge").type(NUMBER)
                                        .description("최소 연령대"),
                                fieldWithPath("endAge").type(NUMBER)
                                        .description("최대 연령대"),
                                fieldWithPath("cost").type(NUMBER)
                                        .description("금액"),
                                fieldWithPath("content").type(STRING)
                                        .description("동행 내용")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.accompanyId").type(NUMBER)
                                        .description("accompany ID"),
                                fieldWithPath("data.startDate").type(STRING)
                                        .description("여행 시작 일자"),
                                fieldWithPath("data.endDate").type(STRING)
                                        .description("여행 종료 일자"),
                                fieldWithPath("data.title").type(STRING)
                                        .description("동행 제목"),
                                fieldWithPath("data.content").type(STRING)
                                        .description("동행 내용"),
                                fieldWithPath("data.userName").type(STRING)
                                        .description("작성자 이름"),
                                fieldWithPath("data.registeredDateTime").type(STRING)
                                        .description("작성 일자"),
                                fieldWithPath("data.views").type(NUMBER)
                                        .description("조회수 (09.20 아직 조회수 기능은 구현 X)"),
                                fieldWithPath("data.likeCount").type(NUMBER)
                                        .description("좋아요 개수 (09.20 아직 좋아요 기능은 구현 X)"),
                                fieldWithPath("data.location").type(STRING)
                                        .description("지역"),
                                fieldWithPath("data.personnel").type(NUMBER)
                                        .description("모집 인원"),
                                fieldWithPath("data.gender").type(STRING)
                                        .description("성별"),
                                fieldWithPath("data.startAge").type(NUMBER)
                                        .description("최소 연령"),
                                fieldWithPath("data.endAge").type(NUMBER)
                                        .description("최대 연령"),
                                fieldWithPath("data.cost").type(NUMBER)
                                        .description("예상 금액"),
                                fieldWithPath("data.userProfileImage").type(STRING)
                                        .description("유저 프로필 이미지")
                        )
                ));
    }

    @Test
    @WithAuthUser
    @DisplayName("동행 모집 글을 조회하는 API")
    void findAllAccompany() throws Exception {
        // given
        AccompanyFindAllResponse findAllResponse = AccompanyFindAllResponse.builder()
                .accompanyId(1L)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .title("동행 제목")
                .content("동행 내용")
                .userName("사용자 이름")
                .registeredDateTime(LocalDateTime.of(2024, 9, 1, 0, 0, 0))
                .userProfileImage("이미지 URL")
                .build();

        SliceResponse<AccompanyFindAllResponse> response = SliceResponse.<AccompanyFindAllResponse>builder()
                .content(List.of(findAllResponse))
                .hasContent(true)
                .number(1)
                .size(4)
                .first(true)
                .last(false)
                .build();
        // when
        given(accompanyService.findAllAccompany(any(OffsetLimit.class)))
                .willReturn(response);

        // then
        mockMvc.perform(get("/v1/accompanies")
                        .param("page", "1")
                        .param("size", "4")
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("accompany/findAll",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").description("페이지")
                                        .optional(),
                                parameterWithName("size").description("페이지 사이즈")
                                        .optional()
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.hasContent").type(BOOLEAN)
                                        .description("데이터 존재 여부"),
                                fieldWithPath("data.isFirst").type(BOOLEAN)
                                        .description("첫 번째 페이지 여부"),
                                fieldWithPath("data.isLast").type(BOOLEAN)
                                        .description("마지막 페이지 여부"),
                                fieldWithPath("data.number").type(NUMBER)
                                        .description("현재 페이지 번호"),
                                fieldWithPath("data.size").type(NUMBER)
                                        .description("토론방 반환 사이즈"),
                                fieldWithPath("data.content[]").type(ARRAY)
                                        .description("여행 일정 데이터"),
                                fieldWithPath("data.content[].accompanyId").type(NUMBER)
                                        .description("accompany ID"),
                                fieldWithPath("data.content[].startDate").type(STRING)
                                        .description("여행 시작 일자"),
                                fieldWithPath("data.content[].endDate").type(STRING)
                                        .description("여행 종료 일자"),
                                fieldWithPath("data.content[].title").type(STRING)
                                        .description("동행 제목"),
                                fieldWithPath("data.content[].content").type(STRING)
                                        .description("동행 내용"),
                                fieldWithPath("data.content[].userName").type(STRING)
                                        .description("작성자 이름"),
                                fieldWithPath("data.content[].registeredDateTime").type(STRING)
                                        .description("작성 일자"),
                                fieldWithPath("data.content[].userProfileImage").type(STRING)
                                        .description("유저 프로필 이미지")
                        )
                ));
    }

    @Test
    @WithAuthUser
    @DisplayName("작성한 동행 모집 글을 조회하는 API")
    void findAllUserAccompany() throws Exception {
        // given
        AccompanyFindAllResponse findAllResponse = AccompanyFindAllResponse.builder()
                .accompanyId(1L)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .title("동행 제목")
                .content("동행 내용")
                .userName("사용자 이름")
                .registeredDateTime(LocalDateTime.of(2024, 9, 1, 0, 0, 0))
                .userProfileImage("이미지 URL")
                .build();

        SliceResponse<AccompanyFindAllResponse> response = SliceResponse.<AccompanyFindAllResponse>builder()
                .content(List.of(findAllResponse))
                .hasContent(true)
                .number(1)
                .size(4)
                .first(true)
                .last(false)
                .build();
        // when
        given(accompanyService.findAllUserAccompanies(anyLong(), any(OffsetLimit.class)))
                .willReturn(response);

        // then
        mockMvc.perform(get("/v1/users/accompanies")
                        .param("page", "1")
                        .param("size", "4")
                        .header(authorizationHeaderName(), mockBearerToken())
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("accompany/findAllByUser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").description("페이지")
                                        .optional(),
                                parameterWithName("size").description("페이지 사이즈")
                                        .optional()
                        ),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("어세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.hasContent").type(BOOLEAN)
                                        .description("데이터 존재 여부"),
                                fieldWithPath("data.isFirst").type(BOOLEAN)
                                        .description("첫 번째 페이지 여부"),
                                fieldWithPath("data.isLast").type(BOOLEAN)
                                        .description("마지막 페이지 여부"),
                                fieldWithPath("data.number").type(NUMBER)
                                        .description("현재 페이지 번호"),
                                fieldWithPath("data.size").type(NUMBER)
                                        .description("토론방 반환 사이즈"),
                                fieldWithPath("data.content[]").type(ARRAY)
                                        .description("여행 일정 데이터"),
                                fieldWithPath("data.content[].accompanyId").type(NUMBER)
                                        .description("accompany ID"),
                                fieldWithPath("data.content[].startDate").type(STRING)
                                        .description("여행 시작 일자"),
                                fieldWithPath("data.content[].endDate").type(STRING)
                                        .description("여행 종료 일자"),
                                fieldWithPath("data.content[].title").type(STRING)
                                        .description("동행 제목"),
                                fieldWithPath("data.content[].content").type(STRING)
                                        .description("동행 내용"),
                                fieldWithPath("data.content[].userName").type(STRING)
                                        .description("작성자 이름"),
                                fieldWithPath("data.content[].registeredDateTime").type(STRING)
                                        .description("작성 일자"),
                                fieldWithPath("data.content[].userProfileImage").type(STRING)
                                        .description("유저 프로필 이미지")
                        )
                ));
    }

    @Test
    @WithAuthUser
    @DisplayName("동행 모집 글을 단 건 조회하는 API")
    void findOneAccompany() throws Exception {
        // given
        AccompanyFindOneResponse response = AccompanyFindOneResponse.builder()
                .accompanyId(1L)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .title("동행 제목")
                .content("동행 내용")
                .userName("사용자 이름")
                .registeredDateTime(LocalDateTime.of(2024, 9, 1, 0, 0, 0))
                .views(0L)
                .likeCount(0L)
                .location("지역")
                .personnel(4)
                .gender(NO_MATTER)
                .startAge(20)
                .endAge(29)
                .cost(1000000)
                .userProfileImage("이미지 URL")
                .build();

        given(accompanyService.findOneAccompany(anyLong()))
                .willReturn(response);

        // when // then
        mockMvc.perform(get("/v1/accompanies/{accompanyId}", 1L)
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("accompany/findOne",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("accompanyId")
                                        .description("Accompany ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.accompanyId").type(NUMBER)
                                        .description("accompany ID"),
                                fieldWithPath("data.startDate").type(STRING)
                                        .description("여행 시작 일자"),
                                fieldWithPath("data.endDate").type(STRING)
                                        .description("여행 종료 일자"),
                                fieldWithPath("data.title").type(STRING)
                                        .description("동행 제목"),
                                fieldWithPath("data.content").type(STRING)
                                        .description("동행 내용"),
                                fieldWithPath("data.userName").type(STRING)
                                        .description("작성자 이름"),
                                fieldWithPath("data.registeredDateTime").type(STRING)
                                        .description("작성 일자"),
                                fieldWithPath("data.views").type(NUMBER)
                                        .description("조회수 (09.20 아직 조회수 기능은 구현 X)"),
                                fieldWithPath("data.likeCount").type(NUMBER)
                                        .description("좋아요 개수 (09.20 아직 좋아요 기능은 구현 X)"),
                                fieldWithPath("data.location").type(STRING)
                                        .description("지역"),
                                fieldWithPath("data.personnel").type(NUMBER)
                                        .description("모집 인원"),
                                fieldWithPath("data.gender").type(STRING)
                                        .description("성별"),
                                fieldWithPath("data.startAge").type(NUMBER)
                                        .description("최소 연령"),
                                fieldWithPath("data.endAge").type(NUMBER)
                                        .description("최대 연령"),
                                fieldWithPath("data.cost").type(NUMBER)
                                        .description("예상 금액"),
                                fieldWithPath("data.userProfileImage").type(STRING)
                                        .description("유저 프로필 이미지")
                        )
                ));
    }

    @Test
    @WithAuthUser
    @DisplayName("동행 모집 글을 수정하는 API")
    void editAccompany() throws Exception {
        // given
        AccompanyEditRequest request = AccompanyEditRequest.builder()
                .startDate(START_DATE)
                .endDate(END_DATE)
                .title("동행 제목 수정")
                .location("여행지 수정")
                .latitude(0.0)
                .longitude(0.0)
                .personnel(3)
                .gender("선호 성별")
                .startAge(20)
                .endAge(29)
                .cost(1000000)
                .content("동행 내용 수정")
                .build();

        // when // then
        mockMvc.perform(patch("/v1/accompanies/{accompanyId}", 1L)
                        .header(authorizationHeaderName(), mockBearerToken())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("accompany/edit",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("accompanyId")
                                        .description("Accompany ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("어세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("startDate").type(STRING)
                                        .description("여행 시작 일자 (YYYY-MM-DD)"),
                                fieldWithPath("endDate").type(STRING)
                                        .description("여행 종료 일자 (YYYY-MM-DD)"),
                                fieldWithPath("title").type(STRING)
                                        .description("동행 제목"),
                                fieldWithPath("location").type(STRING)
                                        .description("지역 이름"),
                                fieldWithPath("latitude").type(NUMBER)
                                        .description("위도"),
                                fieldWithPath("longitude").type(NUMBER)
                                        .description("경도"),
                                fieldWithPath("personnel").type(NUMBER)
                                        .description("모집 인원"),
                                fieldWithPath("gender").type(STRING)
                                        .description("모집 성별 -> man(남성만), woman(여성만), null(상관없음)"),
                                fieldWithPath("startAge").type(NUMBER)
                                        .description("최소 연령대"),
                                fieldWithPath("endAge").type(NUMBER)
                                        .description("최대 연령대"),
                                fieldWithPath("cost").type(NUMBER)
                                        .description("금액"),
                                fieldWithPath("content").type(STRING)
                                        .description("동행 내용")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(NULL)
                                        .description("응답 데이터")
                        )
                ));
    }

    @Test
    @WithAuthUser
    @DisplayName("동행 모집 글을 삭제 하는 API")
    void deleteAccompany() throws Exception {
        // when // then
        mockMvc.perform(delete("/v1/accompanies/{accompanyId}", 1L)
                        .header(authorizationHeaderName(), mockBearerToken())
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("accompany/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("accompanyId")
                                        .description("Accompany ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("어세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(NULL)
                                        .description("응답 데이터")
                        )
                ));
    }

}
