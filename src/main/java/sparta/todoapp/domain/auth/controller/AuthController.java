package sparta.todoapp.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sparta.todoapp.domain.auth.dto.AuthRequestDto;
import sparta.todoapp.domain.auth.dto.AuthSignUpRequestDto;
import sparta.todoapp.domain.auth.service.AuthService;
import sparta.todoapp.global.config.security.jwt.JwtUtil;

/**
 * 로그인과 회원가입과 같은 인증을 담당하는 컨트롤러.
 * 필터에서는 shouldNotFilter 메서드에서 화이트리스트로 통과되도록 하여
 * 이 컨트롤러에서 로그인 및 회원가입 처리를 할 수 있도록 함.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	/**
	 * 회원 가입
	 * @param requestDto username, password 를 가짐
	 */
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@Valid @RequestBody AuthSignUpRequestDto requestDto) {

		authService.signup(requestDto);

		return ResponseEntity.ok().build();
	}

	/**
	 * 로그인
	 * @param requestDto username, password 를 가짐
	 * @return 헤더에 JWT 반환
	 */
	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDto requestDto) {

		String token = authService.login(requestDto);

		return ResponseEntity.ok()
			.header(JwtUtil.AUTHORIZATION_HEADER, token) // 헤더에 토큰 담아서 전달
			.build();
	}
}
