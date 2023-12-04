package sparta.todoapp.global.config.security;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import sparta.todoapp.domain.auth.repository.UserRepository;
import sparta.todoapp.test.UserTest;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsService 테스트")
class CustomUserDetailsServiceTest implements UserTest {

	@Mock
	UserRepository userRepository;

	@InjectMocks
	CustomUserDetailsService userDetailsService;

	@DisplayName("유저네임으로 유저 찾기 성공")
	@Test
	void load_user_by_username_success() {
		// given
		given(userRepository.findByUsername(TEST_USER_NAME)).willReturn(Optional.of(TEST_USER));

		// when
		UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_USER_NAME);

		// then
		assertThat(userDetails.getUsername()).isEqualTo(TEST_USER_NAME);
		assertThat(userDetails.getPassword()).isEqualTo(TEST_USER_PASSWORD);
	}

	@DisplayName("유저네임으로 유저 찾기 실패 - 없는 유저네임")
	@Test
	void load_user_by_username_fail_not_found_username() {
		// given
		given(userRepository.findByUsername(TEST_USER_NAME)).willThrow(new UsernameNotFoundException("회원을 찾을 수 없습니다."));

		// when & then
		assertThatThrownBy(() -> userDetailsService.loadUserByUsername(TEST_USER_NAME))
			.isInstanceOf(UsernameNotFoundException.class)
			.hasMessage("회원을 찾을 수 없습니다.");
	}
}