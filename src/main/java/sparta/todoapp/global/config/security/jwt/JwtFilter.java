package sparta.todoapp.global.config.security.jwt;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "JWT 인증 및 인가")
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {

	}

	/**
	 * shouldNotFilter 는 true 를 반환하면 필터링 통과시키는 메서드.
	 * 로그인과 회원가입 API 는 AuthController 에서 처리하도록 함
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {

		List<String> excludePath = List.of("/api/auth/signup", "/api/auth/login"); // 화이트 리스트
		String path = request.getRequestURI(); // 현재 URL

		return excludePath.stream().anyMatch(path::startsWith); // 현재 URL 이 화이트 리스트에 존재하는지 체크
	}
}
