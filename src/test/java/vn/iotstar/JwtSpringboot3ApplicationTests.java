package vn.iotstar;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import vn.iotstar.models.LoginResponse;
import vn.iotstar.models.LoginUserModel;
import vn.iotstar.models.RegisterUserModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JwtSpringboot3ApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test toàn bộ luồng: Đăng ký -> Đăng nhập lấy JWT -> Truy cập API bảo vệ /users/me")
    void testAuthFlowAndProtectedApi() throws Exception {
        // 1. Đăng ký tài khoản
        RegisterUserModel registerUser = new RegisterUserModel();
        registerUser.setEmail("testuser@hcmute.edu.vn");
        registerUser.setPassword("123456");
        registerUser.setFullName("Nguyen Van A");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("testuser@hcmute.edu.vn"))
                .andExpect(jsonPath("$.fullName").value("Nguyen Van A"));

        // 2. Đăng nhập để lấy JWT Token
        LoginUserModel loginUser = new LoginUserModel();
        loginUser.setEmail("testuser@hcmute.edu.vn");
        loginUser.setPassword("123456");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        LoginResponse loginResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                LoginResponse.class
        );
        String jwtToken = loginResponse.getToken();
        assertThat(jwtToken).isNotBlank();

        // 3. Truy cập API được bảo vệ (/users/me) với Bearer Token -> Phải trả về 200 OK
        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("testuser@hcmute.edu.vn"))
                .andExpect(jsonPath("$.fullName").value("Nguyen Van A"));

        // 4. Truy cập API được bảo vệ mà không có token -> Phải trả về 403 Forbidden
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isForbidden());
    }
}
