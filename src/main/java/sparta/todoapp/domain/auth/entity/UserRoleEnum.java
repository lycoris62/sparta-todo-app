package sparta.todoapp.domain.auth.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRoleEnum {

	USER(Authority.USER),  // 사용자 권한
	ADMIN(Authority.ADMIN);  // 관리자 권한

	private final String authority;

	public static class Authority {
		public static final String USER = "ROLE_USER";
		public static final String ADMIN = "ROLE_ADMIN";
	}
}