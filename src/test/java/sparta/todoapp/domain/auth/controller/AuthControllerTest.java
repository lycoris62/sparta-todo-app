package sparta.todoapp.domain.auth.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import sparta.todoapp.domain.auth.dto.AuthRequestDto;
import sparta.todoapp.domain.auth.service.AuthService;
import sparta.todoapp.global.config.security.MockSpringSecurityFilter;
import sparta.todoapp.global.config.security.WebSecurityConfig;
import sparta.todoapp.global.config.security.jwt.JwtUtil;
import sparta.todoapp.global.error.exception.DuplicateUsernameException;

@DisplayName("인증 컨트롤러 테스트")
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(
	controllers = {AuthController.class},
	excludeFilters = {
		@ComponentScan.Filter(
			type = FilterType.ASSIGNABLE_TYPE,
			classes = WebSecurityConfig.class
		)
	}
)
class AuthControllerTest {

	private MockMvc mvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	AuthService authService;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(springSecurity(new MockSpringSecurityFilter()))
			.alwaysDo(print())
			.build();
	}

	@DisplayName("회원가입 성공")
	@Test
	void signup() throws Exception {
		// given
		String username = "jaeyun";
		String password = "12345678";
		AuthRequestDto authRequestDto = new AuthRequestDto(username, password);

		String jsonString = objectMapper.writeValueAsString(authRequestDto);

		// when - then
		mvc.perform(post("/api/auth/signup")
				.content(jsonString)
				.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@DisplayName("회원가입 실패 - 중복된 유저네임")
	@Test
	void signup_fail_duplicate_username() throws Exception {
		// given
		String username = "jaeyun";
		String password = "12345678";
		AuthRequestDto authRequestDto = new AuthRequestDto(username, password);

		String jsonString = objectMapper.writeValueAsString(authRequestDto);
		doThrow(new DuplicateUsernameException()).when(authService).signup(any(AuthRequestDto.class));

		// when
		ResultActions resultActions = mvc.perform(post("/api/auth/signup")
			.content(jsonString)
			.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
			.accept(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("중복된 username 입니다."));
	}

	@DisplayName("로그인 성공")
	@Test
	void login() throws Exception {
		// given
		String username = "jaeyun";
		String password = "12345678";
		AuthRequestDto authRequestDto = new AuthRequestDto(username, password);

		String jsonString = objectMapper.writeValueAsString(authRequestDto);

		given(authService.login(authRequestDto)).willReturn("token");

		// when - then
		MvcResult mvcResult = mvc.perform(post("/api/auth/login")
				.content(jsonString)
				.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
			)
			.andExpect(status().isOk())
			.andReturn();

		String jwtToken = mvcResult.getResponse().getHeader(JwtUtil.AUTHORIZATION_HEADER);
	}
}