package sparta.todoapp.global.config.security.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import sparta.todoapp.domain.auth.entity.UserRoleEnum;

@Slf4j
@Component
public class JwtUtil {

	public static final String AUTHORIZATION_HEADER = "Authorization"; // Header KEY 값
	public static final String AUTHORIZATION_KEY = "auth"; // 사용자 권한 값의 KEY
	public static final String BEARER_PREFIX = "Bearer "; // Token 식별자
	private final long TOKEN_TIME = 60 * 60 * 1000L; // 토큰 만료시간 60분

	@Value("${jwt.secret.key}")
	private String secretKey;
	private Key key;
	private JwtParser jwtParser;

	@PostConstruct
	public void init() {
		byte[] bytes = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(bytes);
		jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
	}

	/**
	 * 토큰 생성
	 */
	public String createToken(String username, UserRoleEnum role) {
		Date now = new Date();

		return BEARER_PREFIX +
			   Jwts.builder()
				   .setSubject(username) // 사용자 식별자값(ID)
				   .claim(AUTHORIZATION_KEY, role) // 사용자 권한
				   .setExpiration(new Date(now.getTime() + TOKEN_TIME)) // 만료 시간
				   .setIssuedAt(now) // 발급일
				   .signWith(key, SignatureAlgorithm.HS256) // 암호화 알고리즘 (HS256)
				   .compact();
	}

	/**
	 * JWT 토큰 substring
	 */
	public String substringToken(String tokenValue) {
		if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
			return tokenValue.substring(7);
		}
		log.error("Not Found Token");
		throw new NullPointerException("Not Found Token");
	}

	/**
	 * 토큰 검증
	 */
	public boolean isTokenValid(String token) {
		try {
			jwtParser.parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException | SignatureException e) {
			log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
		} catch (ExpiredJwtException e) {
			log.error("Expired JWT token, 만료된 JWT token 입니다.");
		} catch (UnsupportedJwtException e) {
			log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
		} catch (IllegalArgumentException e) {
			log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
		}
		return false;
	}

	/**
	 * 토큰에서 사용자 정보 가져오기
	 */
	public Claims getUserInfoFromToken(String token) {
		return jwtParser.parseClaimsJws(token).getBody();
	}
}
