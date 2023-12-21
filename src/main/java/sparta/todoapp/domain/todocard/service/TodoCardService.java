package sparta.todoapp.domain.todocard.service;

import static java.util.stream.Collectors.groupingBy;
import static sparta.todoapp.global.error.ErrorCode.ACCESS_DENIED;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.todoapp.domain.auth.entity.User;
import sparta.todoapp.domain.todocard.dto.request.TodoCardCreateRequestDto;
import sparta.todoapp.domain.todocard.dto.request.TodoCardEditRequestDto;
import sparta.todoapp.domain.todocard.dto.response.TodoCardDetailResponseDto;
import sparta.todoapp.domain.todocard.dto.response.TodoCardSimpleResponseDto;
import sparta.todoapp.domain.todocard.entity.TodoCard;
import sparta.todoapp.domain.todocard.repository.TodoCardRepository;
import sparta.todoapp.global.error.exception.ServiceException;

/**
 * 할일카드 관련 서비스
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TodoCardService {

	private final TodoCardRepository todoCardRepository;

	/**
	 * 모든 할일카드 리스트를 사용자에 따라 분류하여 반환
	 */
	public Map<String, List<TodoCardSimpleResponseDto>> getTodoCardsByUsername() {

		return todoCardRepository.findAllByOrderByCreatedAtDesc() // 내림차순으로 할일카드 전부 가져오기
			.stream()
			.map(TodoCardSimpleResponseDto::new) // DTO 변환
			.collect(groupingBy(TodoCardSimpleResponseDto::getUsername)); // 사용자 기준으로 분류
	}

	/**
	 * 할일카드 단건 조회
	 */
	public TodoCardDetailResponseDto getTodoCard(Long todoCardId) {

		TodoCard todoCard = todoCardRepository.getTodoCardById(todoCardId);

		return new TodoCardDetailResponseDto(todoCard);
	}

	/**
	 * 할일카드 생성
	 */
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

	/**
	 * 할일카드 수정
	 */
	@Transactional
	public TodoCardDetailResponseDto editTodoCard(Long todoCardId, TodoCardEditRequestDto requestDto, String username) {

		TodoCard todoCard = getValidTodoCard(todoCardId, username); // 사용자가 작성한 할일카드만 가져옴
		todoCard.update(requestDto);

		return new TodoCardDetailResponseDto(todoCard);
	}

	/**
	 * 할일카드 완료
	 */
	@Transactional
	public void finishTodoCard(Long todoCardId, String username) {

		TodoCard todoCard = getValidTodoCard(todoCardId, username); // 사용자가 작성한 할일카드만 가져옴
		todoCard.finish();
	}

	private TodoCard getValidTodoCard(Long todoCardId, String username) {

		TodoCard todoCard = todoCardRepository.getTodoCardById(todoCardId);
		validateRealUser(username, todoCard.getAuthor().getUsername());

		return todoCard;
	}

	private void validateRealUser(String username, String author) {
		if (!author.equals(username)) {
			throw new ServiceException(ACCESS_DENIED);
		}
	}

	/**
	 * 할일카드 삭제
	 */
	@Transactional
	public void delete(Long todoCardId, String username) {

		TodoCard todoCard = getValidTodoCard(todoCardId, username); // 사용자가 작성한 할일카드만 가져옴
		todoCardRepository.delete(todoCard);
	}
}
