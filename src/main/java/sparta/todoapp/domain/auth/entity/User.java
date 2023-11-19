package sparta.todoapp.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.todoapp.domain.model.BaseEntity;

/**
 * 사용자 엔티티.
 */
@Getter
@Entity
@Table(name = "users") // MySQL 에서 USER 는 예약어이므로 s를 붙임
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 에서는 기본 생성자가 필요하므로 최소 접근제어자로 생성
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 10) // 4자 이상, 10자 이하
	private String username;

	@Column(nullable = false, length = 60) // 평문은 8자 이상, 15자 이하이지만, BCrypt 암호화 시 60자로 늘어남
	private String password;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING) // ORDINAL 로 하면 정수로 저장이 되므로 이후 Role 추가 시 문제 생길 여지 있음
	private UserRoleEnum role;

	private User(String username, String password, UserRoleEnum role) {
		this.username = username;
		this.password = password;
		this.role = role;
	}

	public static User createUser(String username, String password) {
		return new User(username, password, UserRoleEnum.USER);
	}
}
