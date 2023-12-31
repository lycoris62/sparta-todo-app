package sparta.todoapp.domain.auth.controller;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sparta.todoapp.global.error.ErrorCode.DUPLICATE_USERNAME;
import static sparta.todoapp.global.error.ErrorCode.USER_NOT_FOUND;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import sparta.todoapp.domain.auth.dto.request.AuthLoginRequestDto;
import sparta.todoapp.domain.auth.dto.request.AuthSignUpRequestDto;
import sparta.todoapp.global.config.security.jwt.JwtUtil;
import sparta.todoapp.global.error.exception.ServiceException;
import sparta.todoapp.test.ControllerTest;

@DisplayName("인증 컨트롤러 테스트")
class AuthControllerTest extends ControllerTest {

	@DisplayName("회원가입 성공")
	@Test
	void signup() throws Exception {
		// given
		AuthSignUpRequestDto authRequestDto = new AuthSignUpRequestDto(TEST_USER_NAME, TEST_USER_PASSWORD, TEST_USER_PASSWORD);

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
		AuthLoginRequestDto authRequestDto = new AuthLoginRequestDto(TEST_USER_NAME, TEST_USER_PASSWORD);

		String jsonString = objectMapper.writeValueAsString(authRequestDto);
		doThrow(new ServiceException(DUPLICATE_USERNAME)).when(authService).signup(any(AuthSignUpRequestDto.class));

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
		AuthLoginRequestDto authRequestDto = new AuthLoginRequestDto(TEST_USER_NAME, TEST_USER_PASSWORD);
		String jsonString = objectMapper.writeValueAsString(authRequestDto);

		given(authService.login(any(AuthLoginRequestDto.class))).willReturn("token");

		// when
		ResultActions resultActions = mvc.perform(post("/api/auth/login")
			.content(jsonString)
			.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
		);

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(header().string(JwtUtil.AUTHORIZATION_HEADER, "token"));
	}

	@DisplayName("로그인 실패 - 유저를 찾을 수 없음")
	@Test
	void login_fail_user_not_found() throws Exception {
		// given
		AuthLoginRequestDto authRequestDto = new AuthLoginRequestDto(TEST_USER_NAME, TEST_USER_PASSWORD);
		String jsonString = objectMapper.writeValueAsString(authRequestDto);

		given(authService.login(any(AuthLoginRequestDto.class))).willThrow(new ServiceException(USER_NOT_FOUND));

		// when
		ResultActions resultActions = mvc.perform(post("/api/auth/login")
			.content(jsonString)
			.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
		);

		// then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("회원을 찾을 수 없습니다."));
	}
}