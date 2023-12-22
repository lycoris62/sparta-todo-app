package sparta.todoapp.domain.auth.repository;

import static sparta.todoapp.global.error.ErrorCode.DUPLICATE_USERNAME;
import static sparta.todoapp.global.error.ErrorCode.USER_NOT_FOUND;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sparta.todoapp.domain.auth.entity.User;
import sparta.todoapp.global.error.exception.ServiceException;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);

	boolean existsByUsername(String username);

	default User getUserByUsername(String username) {
		return findByUsername(username).orElseThrow(() -> new ServiceException(USER_NOT_FOUND));
	}

	default void checkExistingUsername(String username) {
		if (existsByUsername(username)) {
			throw new ServiceException(DUPLICATE_USERNAME);
		}
	}
}
