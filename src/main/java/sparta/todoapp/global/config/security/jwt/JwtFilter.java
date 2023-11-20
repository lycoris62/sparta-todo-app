package sparta.todoapp.global.config.security.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sparta.todoapp.global.error.exception.InvalidTokenException;

/**
 * 헤더에서 JWT 가져와서 인증 객체 생성
 */
@Slf4j(topic = "JWT 인증")
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
		ServletException,
		IOException {

		String tokenValue = request.getHeader(JwtUtil.AUTHORIZATION_HEADER); // 헤더에서 JWT 가져오기
		log.info("tokenValue : {}", tokenValue);

		if (!StringUtils.hasText(tokenValue)) {
			filterChain.doFilter(request, response);
			return;
		}

		tokenValue = jwtUtil.substringToken(tokenValue); // Bearer 접두사 제거

		if (!jwtUtil.isTokenValid(tokenValue)) { // 유효성 검증
			log.error("토큰 에러");
			throw new InvalidTokenException();
		}

		Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
		setAuthentication(info.getSubject());

		filterChain.doFilter(request, response);
	}

	/**
	 * 인증 처리
	 * @param username JWT 속의 Subject 에 저장된 username
	 */
	private void setAuthentication(String username) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		Authentication authentication = createAuthentication(username);
		context.setAuthentication(authentication);

		SecurityContextHolder.setContext(context);
	}

	/**
	 * 인증 객체 생성
	 */
	private Authentication createAuthentication(String username) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
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
