package sparta.todoapp.domain.auth.service;

import static sparta.todoapp.global.error.ErrorCode.PASSWORD_MISMATCH;
import static sparta.todoapp.global.error.ErrorCode.USER_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.todoapp.domain.auth.dto.AuthLoginRequestDto;
import sparta.todoapp.domain.auth.dto.AuthSignUpRequestDto;
import sparta.todoapp.domain.auth.entity.User;
import sparta.todoapp.domain.auth.repository.UserRepository;
import sparta.todoapp.global.config.security.CustomUserDetails;
import sparta.todoapp.global.config.security.jwt.JwtUtil;
import sparta.todoapp.global.error.exception.ServiceException;

/**
 * 로그인과 회원가입과 같이 인증을 담당하는 서비스
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	/**
	 * 회원 가입
	 * @param requestDto username, password 를 가지는 DTO
	 */
	@Transactional
	public void signup(AuthSignUpRequestDto requestDto) {

		validateConfirmPassword(requestDto);

		String username = requestDto.getUsername();
		String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

		User createdUser = createUser(username, encodedPassword);

		userRepository.save(createdUser);
	}

	private void validateConfirmPassword(AuthSignUpRequestDto requestDto) {
		if (!requestDto.getPassword().equals(requestDto.getConfirmPassword())) {
			throw new ServiceException(PASSWORD_MISMATCH);
		}
	}

	private User createUser(String username, String encodedPassword) {
		userRepository.checkExistingUsername(username); // 이미 유저네임이 있는지 검증
		return User.createUser(username, encodedPassword);
	}

	/**
	 * 로그인
	 * @param requestDto username, password 를 가지는 DTO
	 * @return JWT 반환
	 */
	public String login(AuthLoginRequestDto requestDto) {

		User user = userRepository.getUserByUsername(requestDto.getUsername());

		Authentication authentication = getAuthentication(requestDto.getPassword(), user);
		setAuthentication(authentication);

		return jwtUtil.createToken(user.getUsername(), user.getRole()); // 토큰 생성
	}

	private Authentication getAuthentication(String rawPassword, User user) {
		validatePassword(rawPassword, user.getPassword()); // 맞는 비밀번호인지 검증

		CustomUserDetails userDetails = new CustomUserDetails(user);
		return new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), userDetails.getAuthorities());
	}

	private void validatePassword(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new ServiceException(USER_NOT_FOUND);
		}
	}

	private void setAuthentication(Authentication authentication) {
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(authentication);
	}
}
