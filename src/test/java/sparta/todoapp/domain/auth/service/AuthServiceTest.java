package sparta.todoapp.domain.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import sparta.todoapp.domain.auth.dto.AuthRequestDto;
import sparta.todoapp.domain.auth.entity.User;
import sparta.todoapp.domain.auth.entity.UserRoleEnum;
import sparta.todoapp.domain.auth.repository.UserRepository;
import sparta.todoapp.global.config.security.jwt.JwtUtil;
import sparta.todoapp.global.error.exception.DuplicateUsernameException;
import sparta.todoapp.global.error.exception.UserNotFoundException;

@DisplayName("로그인 및 회원가입 요청 서비스 테스트")
@ExtendWith(MockitoExtension.class) // @Mock 사용을 위해 설정합니다.
class AuthServiceTest {

	@Mock
	JwtUtil jwtUtil;
	@Mock
	PasswordEncoder passwordEncoder;
	@Mock
	UserRepository userRepository;

	@InjectMocks
	AuthService authService;

	@DisplayName("회원가입 성공")
	@Test
	void signup_success() {
		// given
		String username = "jaeyun";
		String password = "12345678";
		AuthRequestDto authRequestDto = new AuthRequestDto(username, password);
		User user = User.createUser(username, password);

		given(userRepository.existsByUsername(authRequestDto.getUsername())).willReturn(false);
		given(userRepository.save(any(User.class))).willReturn(user);

		// when
		authService.signup(authRequestDto);

		// then
		assertThat(authRequestDto.getUsername()).isEqualTo(username);
	}

	@DisplayName("회원가입 실패 - 중복된 유저네임")
	@Test
	void signup_fail() {
		// given
		String username = "jaeyun";
		String password = "12345678";
		AuthRequestDto authRequestDto = new AuthRequestDto(username, password);

		given(userRepository.existsByUsername(authRequestDto.getUsername())).willReturn(true);

		// when & then
		assertThatThrownBy(() -> authService.signup(authRequestDto)).isInstanceOf(DuplicateUsernameException.class);
	}

	@DisplayName("로그인 성공")
	@Test
	void login_success() {
		// given
		String username = "jaeyun";
		String password = "12345678";
		UserRoleEnum userRoleEnum = UserRoleEnum.USER;

		AuthRequestDto authRequestDto = new AuthRequestDto(username, password);
		User user = User.createUser(username, password);

		given(userRepository.findByUsername(authRequestDto.getUsername())).willReturn(Optional.of(user));
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
		given(jwtUtil.createToken(authRequestDto.getUsername(), userRoleEnum)).willReturn(anyString());

		// when
		String jwt = authService.login(authRequestDto);

		// then
		assertThat(jwt).isInstanceOf(String.class);
	}

	@DisplayName("로그인 실패 - 없는 유저네임")
	@Test
	void login_fail_invalid_username() {
		// given
		String username = "jaeyun";
		String password = "12345678";
		AuthRequestDto authRequestDto = new AuthRequestDto(username, password);

		given(userRepository.findByUsername(authRequestDto.getUsername())).willThrow(UserNotFoundException.class);

		// when & then
		assertThatThrownBy(() -> authService.login(authRequestDto)).isInstanceOf(UserNotFoundException.class);
	}

	@DisplayName("로그인 실패 - 잘못된 비밀번호")
	@Test
	void login_fail_invalid_password() {
		// given
		String username = "jaeyun";
		String password = "12345678";

		AuthRequestDto authRequestDto = new AuthRequestDto(username, password);
		User user = User.createUser(username, password);

		given(userRepository.findByUsername(authRequestDto.getUsername())).willReturn(Optional.of(user));
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

		// when & then
		assertThatThrownBy(() -> authService.login(authRequestDto)).isInstanceOf(UserNotFoundException.class);
	}
}