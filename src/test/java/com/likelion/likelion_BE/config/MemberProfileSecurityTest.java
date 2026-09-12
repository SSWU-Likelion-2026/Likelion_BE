package com.likelion.likelion_BE.config;

import com.likelion.likelion_BE.config.jwt.JwtAuthenticationFilter;
import com.likelion.likelion_BE.config.jwt.JwtTokenProvider;
import com.likelion.likelion_BE.domain.memberprofile.controller.MemberProfileController;
import com.likelion.likelion_BE.domain.memberprofile.service.MemberProfileService;
import com.likelion.likelion_BE.domain.user.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig(MemberProfileSecurityTest.TestConfig.class)
class MemberProfileSecurityTest {
    @Configuration
    @EnableWebMvc
    @Import({SecurityConfig.class, MemberProfileController.class})
    static class TestConfig {
        @Bean
        MemberProfileService memberProfileService() {
            return mock(MemberProfileService.class);
        }

        @Bean
        JwtAuthenticationFilter jwtAuthenticationFilter() {
            return new JwtAuthenticationFilter(mock(JwtTokenProvider.class), mock(CustomUserDetailsService.class));
        }
    }

    @Autowired WebApplicationContext context;
    @Autowired FilterChainProxy securityFilterChain;
    @Autowired MemberProfileService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        reset(service);
        mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(securityFilterChain).build();
    }

    @Test
    void listAndDetailAllowRequestsWithoutToken() throws Exception {
        mvc.perform(get("/api/v1/member-profiles").param("term", "14"))
                .andExpect(status().isOk());
        verify(service).getProfiles(14, null, null);

        mvc.perform(get("/api/v1/member-profiles/1"))
                .andExpect(status().isOk());
        verify(service).getProfile(1L);
    }

    @Test
    void myProfileAndWritesRequireAuthentication() throws Exception {
        for (HttpMethod method : new HttpMethod[]{HttpMethod.GET, HttpMethod.POST, HttpMethod.PATCH, HttpMethod.DELETE}) {
            mvc.perform(request(method, "/api/v1/member-profiles/me").param("term", "14"))
                    .andExpect(status().isForbidden());
        }
        for (HttpMethod method : new HttpMethod[]{HttpMethod.POST, HttpMethod.PATCH, HttpMethod.DELETE}) {
            mvc.perform(request(method, "/api/v1/member-profiles/1"))
                    .andExpect(status().isForbidden());
        }
        verifyNoInteractions(service);
    }

    @Test
    void guestCannotCreateMemberProfile() throws Exception {
        mvc.perform(post("/api/v1/member-profiles/me")
                        .with(user("guest@example.com").authorities(() -> "GUEST"))
                        .contentType("application/json")
                        .content(validCreateRequest()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(service);
    }

    @Test
    void memberLeaderAndManagerCanCreateMemberProfile() throws Exception {
        for (String role : new String[]{"MEMBER", "LEADER", "MANAGER"}) {
            mvc.perform(post("/api/v1/member-profiles/me")
                            .with(user(role.toLowerCase() + "@example.com").authorities(() -> role))
                            .contentType("application/json")
                            .content(validCreateRequest()))
                    .andExpect(status().isCreated());
        }

        verify(service, times(3)).createMyProfile(any(), any());
    }

    private String validCreateRequest() {
        return """
                {
                  "term": 14,
                  "name": "홍길동",
                  "department": "컴퓨터공학과",
                  "studentId": "20260001",
                  "memberGroup": "FE",
                  "memberType": "BABY_LION",
                  "position": "NONE"
                }
                """;
    }
}
