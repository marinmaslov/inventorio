package com.inventorio;

import com.inventorio.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanup() {
        userRepository.findByUsername("testuser")
                .ifPresent(user -> userRepository.deleteById(user.getId()));
    }

    @Test
    void testRegisterAndLogin() throws Exception {
        String username = "testuser";
        String password = "testpass";

        // Register
        String registerResponse = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("username=" + username + "&password=" + password))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(registerResponse).contains("registered successfully");

        // Login
        String token = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("username=" + username + "&password=" + password))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(token.length()).isGreaterThan(10);
    }
}
