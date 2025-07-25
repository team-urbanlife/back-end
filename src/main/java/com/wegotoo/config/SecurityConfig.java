package com.wegotoo.config;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.wegotoo.infra.security.handler.CustomAuthenticationEntryPoint;
import com.wegotoo.infra.security.jwt.filter.JwtAuthorizationFilter;
import com.wegotoo.infra.security.oauth.CustomOAuth2UserService;
import com.wegotoo.infra.security.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import com.wegotoo.infra.security.oauth.handler.OAuth2FailureHandler;
import com.wegotoo.infra.security.oauth.handler.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint entryPoint;
    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final JwtAuthorizationFilter authorizationFilter;
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(
                        STATELESS))
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/oauth2/**").permitAll()
                                .requestMatchers("/v1/reissue").permitAll()
                                .requestMatchers("/v1/app/reissue").permitAll()
                                .requestMatchers("/docs/**").permitAll()
                                .requestMatchers(GET, "/v1/accompanies/**").permitAll()
                                .requestMatchers("/ws/**").permitAll()
                                .requestMatchers("/v1/notification/**").permitAll()
                                .requestMatchers("/v1/s3/**").permitAll()
                                .requestMatchers("/v1/cities").permitAll()
                                .requestMatchers(GET, "/v1/posts/**").permitAll()
                                .requestMatchers("/actuator/**").permitAll()
                                .anyRequest().authenticated())
                .exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer.authenticationEntryPoint(entryPoint))
                .oauth2Login(oauth2LoginConfigurer ->
                        oauth2LoginConfigurer
                                .authorizationEndpoint(authorizationEndpointConfig ->
                                        authorizationEndpointConfig.baseUri("/oauth2/authorization")
                                                .authorizationRequestRepository(authorizationRequestRepository))
                                .redirectionEndpoint(redirectionEndpointConfig ->
                                        redirectionEndpointConfig.baseUri("/login/oauth2/code/*"))
                                .userInfoEndpoint(userInfoEndpointConfig ->
                                        userInfoEndpointConfig.userService(oAuth2UserService))
                                .successHandler(oAuth2SuccessHandler)
                                .failureHandler(oAuth2FailureHandler));

        http.addFilterBefore(authorizationFilter, LogoutFilter.class);

        return http.build();
    }

}
