package com.wegotoo.support;

import com.wegotoo.api.accompany.AccompanyController;
import com.wegotoo.api.auth.AuthController;
import com.wegotoo.api.chat.ChatController;
import com.wegotoo.api.chatroom.ChatRoomController;
import com.wegotoo.api.city.CityController;
import com.wegotoo.api.like.PostLikeController;
import com.wegotoo.api.post.PostController;
import com.wegotoo.api.s3.S3Controller;
import com.wegotoo.api.schedule.DetailedPlanController;
import com.wegotoo.api.schedule.MemoController;
import com.wegotoo.api.schedule.ScheduleController;
import com.wegotoo.api.schedule.ScheduleDetailsController;
import com.wegotoo.api.user.UserController;
import com.wegotoo.config.SecurityConfig;
import com.wegotoo.config.WebConfig;
import com.wegotoo.support.config.TestWebConfig;
import jakarta.servlet.Filter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@WebMvcTest(controllers = {
        DetailedPlanController.class,
        MemoController.class,
        ScheduleController.class,
        ScheduleDetailsController.class,
        ChatRoomController.class,
        AccompanyController.class,
        CityController.class,
        AuthController.class,
        S3Controller.class,
        PostController.class,
        ChatController.class,
        PostLikeController.class,
        UserController.class
},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                SecurityConfig.class,
                                WebConfig.class,
                                Filter.class
                        })
        })
@AutoConfigureMockMvc(addFilters = false)
@Import(TestWebConfig.class)
public @interface ControllerWebMvcTest {
}
