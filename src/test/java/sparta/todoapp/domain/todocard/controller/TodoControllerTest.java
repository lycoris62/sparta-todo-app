package sparta.todoapp.domain.todocard.controller;

import static java.util.stream.Collectors.groupingBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sparta.todoapp.global.error.ErrorCode.ACCESS_DENIED;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import sparta.todoapp.domain.auth.entity.User;
import sparta.todoapp.domain.model.BaseEntity;
import sparta.todoapp.domain.todocard.dto.request.TodoCardCreateRequestDto;
import sparta.todoapp.domain.todocard.dto.request.TodoCardEditRequestDto;
import sparta.todoapp.domain.todocard.dto.response.TodoCardDetailResponseDto;
import sparta.todoapp.domain.todocard.dto.response.TodoCardSimpleResponseDto;
import sparta.todoapp.domain.todocard.entity.TodoCard;
import sparta.todoapp.global.error.exception.ServiceException;
import sparta.todoapp.test.ControllerTest;
import sparta.todoapp.test.TodoCardTest;

@Disabled
@DisplayName("할일카드 컨트롤러 테스트")
class TodoControllerTest extends ControllerTest implements TodoCardTest {

	@DisplayName("모든 할일카드를 username 으로 분류하여 응답 성공")
	@Test
	void get_todocards_success() throws Exception {
		// given
		List<TodoCard> todoCardList = new ArrayList<>(List.of(TEST_TODO_CARD_01, TEST_TODO_CARD_02, TEST_TODO_CARD_03));
		todoCardList.sort(Comparator.comparing(BaseEntity::getCreatedAt).reversed());

		given(todoCardRepository.findAllByOrderByCreatedAtDesc()).willReturn(todoCardList);

		Map<String, List<TodoCardSimpleResponseDto>> result = todoCardRepository.findAllByOrderByCreatedAtDesc()
			.stream()
			.map(TodoCardSimpleResponseDto::new) // DTO 변환
			.collect(groupingBy(TodoCardSimpleResponseDto::getUsername));// 사용자 기준으로 분류

		given(todoCardService.getTodoCardsByUsername()).willReturn(result);

		// when
		ResultActions resultActions = mvc.perform(get("/api/todocards")
			.accept(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(2))
			.andExpect(jsonPath("$." + TEST_USER_NAME + ".size()").value(2))
			.andExpect(jsonPath("$." + TEST_ANOTHER_USER_NAME + ".size()").value(1))
			.andExpect(jsonPath("$." + TEST_USER_NAME + "[0].title").value(TEST_TODO_CARD_TITLE_03))
			.andExpect(jsonPath("$." + TEST_USER_NAME + "[0].username").value(TEST_USER_NAME))
			.andExpect(jsonPath("$." + TEST_ANOTHER_USER_NAME + "[0].title").value(TEST_TODO_CARD_TITLE_02))
			.andExpect(jsonPath("$." + TEST_ANOTHER_USER_NAME + "[0].username").value(TEST_ANOTHER_USER_NAME));
	}

	@DisplayName("할일카드 단건 조회 응답 성공")
	@Test
	void get_todocard_success() throws Exception {
		// given
		given(todoCardRepository.findById(TODO_CARD_ID_01)).willReturn(Optional.of(TEST_TODO_CARD_01));
		TodoCardDetailResponseDto responseDto = new TodoCardDetailResponseDto(
			todoCardRepository.findById(TODO_CARD_ID_01).get());
		given(todoCardService.getTodoCard(TODO_CARD_ID_01)).willReturn(responseDto);

		// when
		ResultActions resultActions = mvc.perform(get("/api/todocards/{todoCardId}", TODO_CARD_ID_01)
			.accept(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value(TEST_TODO_CARD_TITLE_01))
			.andExpect(jsonPath("$.content").value(TEST_TODO_CARD_CONTENT_01))
			.andExpect(jsonPath("$.username").value(TEST_USER_NAME))
			.andExpect(jsonPath("$.comments.size()").value(TEST_TODO_CARD_01.getCommentList().size()));
	}

	@DisplayName("할일카드 단건 조회 응답 실패 - 없는 할일카드")
	@Test
	void get_todocard_fail_not_found() throws Exception {
		// given
		Long todoCardId = 100000L;
		given(todoCardService.getTodoCard(todoCardId)).willThrow(new IllegalArgumentException("잘못된 아이디"));

		// when
		ResultActions resultActions = mvc.perform(get("/api/todocards/{todoCardId}", todoCardId)
			.accept(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못된 아이디"));
	}

	@DisplayName("할일카드 생성 성공")
	@Test
	void create_todocard_success() throws Exception {
		// given
		TodoCardCreateRequestDto requestDto = new TodoCardCreateRequestDto(TEST_TODO_CARD_TITLE_01,
			TEST_TODO_CARD_CONTENT_01, NOW);

		String json = objectMapper.writeValueAsString(requestDto);
		TodoCardDetailResponseDto responseDto = new TodoCardDetailResponseDto(TEST_TODO_CARD_01);

		given(todoCardService.createTodoCard(any(TodoCardCreateRequestDto.class), any(User.class)))
			.willReturn(responseDto);

		// when
		ResultActions resultActions = mvc.perform(post("/api/todocards")
			.content(json)
			.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
			.accept(MediaType.APPLICATION_JSON)
			.principal(mockPrincipal));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value(TEST_TODO_CARD_TITLE_01))
			.andExpect(jsonPath("$.content").value(TEST_TODO_CARD_CONTENT_01))
			.andExpect(jsonPath("$.username").value(TEST_USER_NAME))
			.andExpect(jsonPath("$.comments.size()").value(TEST_TODO_CARD_01.getCommentList().size()));
	}

	@DisplayName("할일 카드 수정 실패 - 다른 작성자가 수정하려고 접근")
	@Test
	void edit_todocard_fail_not_same_user() throws Exception {
		// given
		TodoCardEditRequestDto requestDto = new TodoCardEditRequestDto(TEST_TODO_CARD_TITLE_01, TEST_TODO_CARD_CONTENT_01);

		String json = objectMapper.writeValueAsString(requestDto);
		given(todoCardService.editTodoCard(anyLong(), any(TodoCardEditRequestDto.class), anyString()))
			.willThrow(new ServiceException(ACCESS_DENIED));

		// when
		ResultActions resultActions = mvc.perform(patch("/api/todocards/{todoCardId}", TODO_CARD_ID_01)
			.content(json)
			.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
			.accept(MediaType.APPLICATION_JSON)
			.principal(mockPrincipal));

		// then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("작성자만 삭제/수정할 수 있습니다."));
	}
}