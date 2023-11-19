package sparta.todoapp.domain.todocard.service;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sparta.todoapp.domain.auth.entity.User;
import sparta.todoapp.domain.todocard.dto.request.TodoCardCreateRequestDto;
import sparta.todoapp.domain.todocard.dto.request.TodoCardEditRequestDto;
import sparta.todoapp.domain.todocard.dto.response.TodoCardDetailResponseDto;
import sparta.todoapp.domain.todocard.dto.response.TodoCardSimpleResponseDto;
import sparta.todoapp.domain.todocard.entity.TodoCard;
import sparta.todoapp.domain.todocard.repository.TodoCardRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TodoCardService {

	private final TodoCardRepository todoCardRepository;

	public Map<String, List<TodoCardSimpleResponseDto>> getTodoCards() {

		return todoCardRepository.findAllByOrderByCreatedAtDesc() // 내림차순으로 할일카드 전부 가져오기
			.stream()
			.map(TodoCardSimpleResponseDto::new) // DTO 변환
			.collect(groupingBy(TodoCardSimpleResponseDto::getUsername)); // 사용자 기준으로 분류
	}

	public TodoCardDetailResponseDto getTodoCard(Long todoCardId) {

		TodoCard todoCard = getTodoCardById(todoCardId);

		return new TodoCardDetailResponseDto(todoCard);
	}

	@Transactional
	public TodoCardDetailResponseDto createTodoCard(TodoCardCreateRequestDto requestDto, User user) {

		TodoCard todoCard = TodoCard.builder()
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.author(user)
			.createdAt(requestDto.getCreatedAt())
			.build();

		TodoCard savedTodoCard = todoCardRepository.save(todoCard);

		return new TodoCardDetailResponseDto(savedTodoCard);
	}

	@Transactional
	public TodoCardDetailResponseDto editTodoCard(Long todoCardId, TodoCardEditRequestDto requestDto, String username) {

		TodoCard todoCard = getValidTodoCard(todoCardId, username);
		todoCard.update(requestDto);

		return new TodoCardDetailResponseDto(todoCard);
	}

	@Transactional
	public void finishTodoCard(Long todoCardId, String username) {

		TodoCard todoCard = getValidTodoCard(todoCardId, username);
		todoCard.finish();
	}

	private TodoCard getTodoCardById(Long todoCardId) {

		return todoCardRepository.findById(todoCardId)
			.orElseThrow(() -> new IllegalArgumentException("잘못된 아이디"));
	}

	private TodoCard getValidTodoCard(Long todoCardId, String username) {

		TodoCard todoCard = getTodoCardById(todoCardId);
		validateRealUser(username, todoCard.getAuthor().getUsername());

		return todoCard;
	}

	private void validateRealUser(String username, String author) {
		if (!author.equals(username)) {
			log.error("user : {}", username);
			log.error("todo author : {}", author);
			throw new AccessDeniedException("자신의 할일카드가 아닙니다.");
		}
	}
}