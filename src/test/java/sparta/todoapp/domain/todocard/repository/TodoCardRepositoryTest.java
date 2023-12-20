package sparta.todoapp.domain.todocard.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import sparta.todoapp.domain.auth.entity.User;
import sparta.todoapp.domain.auth.repository.UserRepository;
import sparta.todoapp.domain.todocard.entity.TodoCard;
import sparta.todoapp.test.TestQueryDslConfig;

@Import(TestQueryDslConfig.class)
@ActiveProfiles("test")
@DisplayName("할일카드 레포지토리 테스트")
@DataJpaTest
class TodoCardRepositoryTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	TodoCardRepository todoCardRepository;

	@DisplayName("생성일자를 기준으로 내림차순한 할일카드 리스트 조회")
	@Test
	void read_todocard_list_order_by_created_at() {
		// given
		String username = "jaeyun";
		String password = "12345678";
		User user = userRepository.save(User.createUser(username, password));

		LocalDateTime now = LocalDateTime.now();
		TodoCard todoCard1 = todoCardRepository.save(new TodoCard("title01", "content01", user, now.minusDays(1)));
		TodoCard todoCard2 = todoCardRepository.save(new TodoCard("title02", "content02", user, now.minusDays(2)));
		TodoCard todoCard3 = todoCardRepository.save(new TodoCard("title03", "content03", user, now.minusDays(3)));

		// when
		List<TodoCard> todoCardList = todoCardRepository.findAllByOrderByCreatedAtDesc();

		// given
		Assertions.assertThat(todoCardList.get(0).getTitle()).isEqualTo(todoCard1.getTitle());
		Assertions.assertThat(todoCardList.get(1).getTitle()).isEqualTo(todoCard2.getTitle());
		Assertions.assertThat(todoCardList.get(2).getTitle()).isEqualTo(todoCard3.getTitle());
	}
}