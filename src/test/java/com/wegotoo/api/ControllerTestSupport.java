package com.wegotoo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wegotoo.application.accompany.AccompanyService;
import com.wegotoo.application.chatroom.ChatRoomService;
import com.wegotoo.application.schedule.DetailedPlanService;
import com.wegotoo.application.schedule.MemoService;
import com.wegotoo.application.schedule.ScheduleDetailsService;
import com.wegotoo.application.schedule.ScheduleService;
import com.wegotoo.support.ControllerWebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@ControllerWebMvcTest
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected DetailedPlanService detailedPlanService;

    @MockBean
    protected MemoService memoService;

    @MockBean
    protected ScheduleService scheduleService;

    @MockBean
    protected ScheduleDetailsService scheduleDetailsService;

    @MockBean
    protected ChatRoomService chatRoomService;

    @MockBean
    protected AccompanyService accompanyService;

}
