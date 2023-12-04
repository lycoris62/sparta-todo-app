package sparta.todoapp.global.config.security.jwt;

import static org.assertj.core.api.Assertions.*;
import static sparta.todoapp.global.config.security.jwt.JwtUtil.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import sparta.todoapp.domain.auth.entity.UserRoleEnum;
import sparta.todoapp.test.UserTest;

@SpringBootTest
@ActiveProfiles("test")
class JwtUtilTest implements UserTest {

	@Autowired
	JwtUtil jwtUtil;

	@Mock
	private HttpServletRequest request;

	@BeforeEach
	void setUp() {
		jwtUtil.init();
	}

	@DisplayName("토큰 생성")
	@Test
	void createToken() {
		// when
		String token = jwtUtil.createToken(TEST_USER_NAME, UserRoleEnum.USER);

		// then
		assertThat(token).isNotNull();
	}

	@DisplayName("토큰 추출")
	@Test
	void resolveToken() {
		// given
		var token = "test-token";
		var bearerToken = BEARER_PREFIX + token;

		// when
		String resolvedToken = jwtUtil.substringToken(bearerToken);

		// then
		assertThat(resolvedToken).isEqualTo(token);
	}

	@DisplayName("토큰 검증")
	@Nested
	class validateToken {

		@DisplayName("토큰 검증 성공")
		@Test
		void validateToken_success() {
			// given
			String token = jwtUtil.createToken(TEST_USER_NAME, UserRoleEnum.USER).substring(7);

			// when
			boolean isValid = jwtUtil.isTokenValid(token);

			// then
			assertThat(isValid).isTrue();
		}

		@DisplayName("토큰 검증 실패 - 유효하지 않은 토큰")
		@Test
		void validateToken_fail() {
			// given
			String invalidToken = "invalid-token";

			// when
			boolean isValid = jwtUtil.isTokenValid(invalidToken);

			// then
			assertThat(isValid).isFalse();
		}

		@DisplayName("토큰에서 UserInfo 조회")
		@Test
		void getUserInfoFromToken() {
			// given
			String token = jwtUtil.createToken(TEST_USER_NAME, UserRoleEnum.USER).substring(7);

			// when
			Claims claims = jwtUtil.getUserInfoFromToken(token);

			// then
			assertThat(claims).isNotNull();
			assertThat(TEST_USER_NAME).isEqualTo(claims.getSubject());
		}
	}
}