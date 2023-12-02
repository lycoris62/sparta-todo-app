package sparta.todoapp.domain.auth.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import sparta.todoapp.domain.auth.entity.User;

@DisplayName("인증 레포지토리 테스트")
@DataJpaTest
class UserRepositoryTest {

	@Autowired
	UserRepository userRepository;

	PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@DisplayName("username 으로 유저 엔티티 찾기")
	@Test
	void find_by_username() {
		// given
		String username = "jaeyun";
		String password = "12345678";
		String encodedPassword = passwordEncoder.encode(password);
		User savedUser = userRepository.save(User.createUser(username, encodedPassword));

		// when
		Optional<User> optionalUser = userRepository.findByUsername(username);

		// then
		assertThat(optionalUser.isPresent()).isEqualTo(true);
		assertThat(optionalUser.get()).isEqualTo(savedUser);
	}

	@DisplayName("없는 username 으로 유저 엔티티 찾기")
	@Test
	void find_by_username_fail() {
		// given
		String username = "jaeyun";
		String password = "12345678";
		String encodedPassword = passwordEncoder.encode(password);
		userRepository.save(User.createUser(username, encodedPassword));

		// when
		Optional<User> optionalUser = userRepository.findByUsername("no username");

		// then
		assertThat(optionalUser.isEmpty()).isEqualTo(true);
	}

	@DisplayName("username이 존재하는지 검사")
	@Test
	void exists_by_username() {
		// given
		String username = "jaeyun";
		String password = "12345678";
		String encodedPassword = passwordEncoder.encode(password);
		User savedUser = userRepository.save(User.createUser(username, encodedPassword));

		// when
		boolean isUsernameExists = userRepository.existsByUsername(savedUser.getUsername());

		// then
		assertThat(isUsernameExists).isEqualTo(true);
	}

	@DisplayName("없는 username이 존재하는지 검사")
	@Test
	void exists_by_username_fail() {
		// given
		String username = "jaeyun";
		String password = "12345678";
		String encodedPassword = passwordEncoder.encode(password);
		userRepository.save(User.createUser(username, encodedPassword));

		// when
		boolean isUsernameExists = userRepository.existsByUsername("no username");

		// then
		assertThat(isUsernameExists).isEqualTo(false);
	}
}