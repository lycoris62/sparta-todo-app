package sparta.todoapp.domain.todocard.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import sparta.todoapp.domain.auth.entity.User;
import sparta.todoapp.domain.todocard.dto.request.TodoCardCreateRequestDto;
import sparta.todoapp.domain.todocard.dto.request.TodoCardEditRequestDto;
import sparta.todoapp.domain.todocard.dto.response.TodoCardDetailResponseDto;
import sparta.todoapp.domain.todocard.dto.response.TodoCardSimpleResponseDto;
import sparta.todoapp.domain.todocard.entity.TodoCard;
import sparta.todoapp.domain.todocard.repository.TodoCardRepository;
import sparta.todoapp.global.error.exception.AccessDeniedException;

@ActiveProfiles("test")
@DisplayName("할일카드의 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class TodoCardServiceTest {

	@Mock
	TodoCardRepository todoCardRepository;

	@InjectMocks
	TodoCardService todoCardService;

	@DisplayName("할일카드 전체 조회 성공")
	@Test
	void get_todocards_success() {
		// given
		String username1 = "jaeyun1";
		String password1 = "12345678";
		User user1 = User.createUser(username1, password1);

		String username2 = "jaeyun2";
		String password2 = "12345678";
		User user2 = User.createUser(username2, password2);

		String title = "title";
		String content = "content";
		LocalDateTime now = LocalDateTime.now();
		TodoCard todoCard1 = new TodoCard(title + "1", content + "1", user1, now.minusMinutes(3));
		TodoCard todoCard2 = new TodoCard(title + "2", content + "2", user2, now.minusDays(2));
		TodoCard todoCard3 = new TodoCard(title + "3", content + "3", user1, now.minusMonths(1));

		given(todoCardRepository.findAllByOrderByCreatedAtDesc()).willReturn(List.of(todoCard1, todoCard2, todoCard3));

		// when
		Map<String, List<TodoCardSimpleResponseDto>> result = todoCardService.getTodoCardsByUsername();

		// then
		assertThat(result.size()).isEqualTo(2);
		assertThat(result.get(username1).size()).isEqualTo(2);
		assertThat(result.get(username2).size()).isEqualTo(1);
		assertThat(result.get(username1).get(0).getCreatedAt()).isEqualTo(now.minusMinutes(3));
		assertThat(result.get(username2).get(0).getTitle()).isEqualTo(title + "2");
	}

	@DisplayName("할일카드 단건 조회 성공")
	@Test
	void get_todocard_success() {
		// given
		String username = "jaeyun";
		String password = "12345678";
		User user = User.createUser(username, password);

		Long todoCardId = 1L;
		String title = "title01";
		String content = "content01";
		LocalDateTime now = LocalDateTime.now();
		TodoCard todoCard = new TodoCard(title, content, user, now);

		given(todoCardRepository.findById(todoCardId)).willReturn(Optional.of(todoCard));

		// when
		TodoCardDetailResponseDto findTodoCard = todoCardService.getTodoCard(todoCardId);

		// then
		assertThat(findTodoCard.getTitle()).isEqualTo(title);
		assertThat(findTodoCard.getContent()).isEqualTo(content);
		assertThat(findTodoCard.getUsername()).isEqualTo(username);
		assertThat(findTodoCard.getComments()).isEqualTo(Collections.emptyList());
		assertThat(findTodoCard.getCreatedAt()).isEqualTo(now);
	}

	@DisplayName("할일카드 단건 조회 실패 - 없는 할일카드 아이디 입력")
	@Test
	void get_todocard_fail() {
		// given
		String username = "jaeyun";
		String password = "12345678";
		User user = User.createUser(username, password);

		Long todoCardId = 2L;
		String title = "title01";
		String content = "content01";
		LocalDateTime now = LocalDateTime.now();
		new TodoCard(title, content, user, now);

		given(todoCardRepository.findById(todoCardId)).willThrow(new IllegalArgumentException("잘못된 아이디"));

		// when & then
		assertThatThrownBy(() -> todoCardService.getTodoCard(todoCardId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("잘못된 아이디");
	}

	@DisplayName("할일카드 생성 성공")
	@Test
	void create_todocard_success() {
		// given
		String username = "jaeyun";
		String password = "12345678";
		User user = User.createUser(username, password);

		String title = "title01";
		String content = "content01";
		LocalDateTime now = LocalDateTime.now();
		TodoCardCreateRequestDto requestDto = new TodoCardCreateRequestDto(title, content, now);

		TodoCard todoCard = TodoCard.builder()
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.author(user)
			.createdAt(requestDto.getCreatedAt())
			.build();

		given(todoCardRepository.save(any(TodoCard.class))).willReturn(todoCard);

		// when
		TodoCardDetailResponseDto savedResponseDto = todoCardService.createTodoCard(requestDto, user);

		// then
		assertThat(savedResponseDto.getTitle()).isEqualTo(title);
		assertThat(savedResponseDto.getContent()).isEqualTo(content);
		assertThat(savedResponseDto.getUsername()).isEqualTo(username);
		assertThat(savedResponseDto.getComments()).isEqualTo(Collections.emptyList());
		assertThat(savedResponseDto.getCreatedAt()).isEqualTo(now);
	}

	@DisplayName("할일카드 수정 성공")
	@Test
	void edit_todocard_success() {
		// given
		String username = "jaeyun";
		String password = "12345678";
		User user = User.createUser(username, password);

		Long todoCardId = 1L;
		String title = "title01";
		String content = "content01";
		LocalDateTime now = LocalDateTime.now();
		TodoCardCreateRequestDto createRequestDto = new TodoCardCreateRequestDto(title, content, now);
		TodoCardEditRequestDto editRequestDto = new TodoCardEditRequestDto(title, content);

		TodoCard todoCard = TodoCard.builder()
			.title(createRequestDto.getTitle())
			.content(createRequestDto.getContent())
			.author(user)
			.createdAt(createRequestDto.getCreatedAt())
			.build();

		given(todoCardRepository.findById(todoCardId)).willReturn(Optional.of(todoCard));

		// when
		TodoCardDetailResponseDto responseDto = todoCardService.editTodoCard(todoCardId, editRequestDto, username);

		// then
		assertThat(responseDto.getTitle()).isEqualTo(title);
		assertThat(responseDto.getContent()).isEqualTo(content);
		assertThat(responseDto.getUsername()).isEqualTo(username);
		assertThat(responseDto.getComments()).isEqualTo(Collections.emptyList());
		assertThat(responseDto.getCreatedAt()).isEqualTo(now);
	}

	@DisplayName("할일카드 수정/완료 실패 - 없는 할일카드")
	@Test
	void edit_todocard_fail_not_found_todocard_id() {
		// given
		String username = "jaeyun";
		String password = "12345678";
		User user = User.createUser(username, password);

		Long todoCardId = 2L;
		String title = "title01";
		String content = "content01";
		LocalDateTime now = LocalDateTime.now();
		TodoCardCreateRequestDto createRequestDto = new TodoCardCreateRequestDto(title, content, now);
		TodoCardEditRequestDto editRequestDto = new TodoCardEditRequestDto(title, content);

		TodoCard.builder()
			.title(createRequestDto.getTitle())
			.content(createRequestDto.getContent())
			.author(user)
			.createdAt(createRequestDto.getCreatedAt())
			.build();

		given(todoCardRepository.findById(todoCardId)).willThrow(new IllegalArgumentException("잘못된 아이디"));

		// when & then
		assertThatThrownBy(() -> todoCardService.editTodoCard(todoCardId, editRequestDto, username))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("잘못된 아이디");
	}

	@DisplayName("할일카드 수정/완료 실패 - 다른 유저네임")
	@Test
	void edit_todocard_fail_not_smae_username() {
		// given
		String username = "jaeyun";
		String password = "12345678";
		User user = User.createUser(username, password);

		Long todoCardId = 2L;
		String title = "title01";
		String content = "content01";
		LocalDateTime now = LocalDateTime.now();
		TodoCardCreateRequestDto createRequestDto = new TodoCardCreateRequestDto(title, content, now);
		TodoCardEditRequestDto editRequestDto = new TodoCardEditRequestDto(title, content);

		TodoCard.builder()
			.title(createRequestDto.getTitle())
			.content(createRequestDto.getContent())
			.author(user)
			.createdAt(createRequestDto.getCreatedAt())
			.build();

		given(todoCardRepository.findById(todoCardId)).willThrow(new AccessDeniedException());

		// when & then
		assertThatThrownBy(() -> todoCardService.editTodoCard(todoCardId, editRequestDto, username))
			.isInstanceOf(AccessDeniedException.class);
	}

	@DisplayName("할일카드 완료 성공")
	@Test
	void finish_todocard_success() {
		// given
		String username = "jaeyun";
		String password = "12345678";
		User user = User.createUser(username, password);

		Long todoCardId = 1L;
		String title = "title01";
		String content = "content01";
		LocalDateTime now = LocalDateTime.now();
		TodoCardCreateRequestDto createRequestDto = new TodoCardCreateRequestDto(title, content, now);

		TodoCard todoCard = TodoCard.builder()
			.title(createRequestDto.getTitle())
			.content(createRequestDto.getContent())
			.author(user)
			.createdAt(createRequestDto.getCreatedAt())
			.build();

		given(todoCardRepository.findById(todoCardId)).willReturn(Optional.of(todoCard));

		// when
		todoCardService.finishTodoCard(todoCardId, username);

		// then
		assertThat(todoCard.isDone()).isEqualTo(true);
	}
}