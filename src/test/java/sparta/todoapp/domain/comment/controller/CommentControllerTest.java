package sparta.todoapp.domain.comment.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import sparta.todoapp.domain.auth.entity.User;
import sparta.todoapp.domain.comment.dto.request.CommentCreateRequestDto;
import sparta.todoapp.domain.comment.dto.request.CommentEditRequestDto;
import sparta.todoapp.domain.comment.dto.response.CommentResponseDto;
import sparta.todoapp.test.CommentTest;
import sparta.todoapp.test.ControllerTest;

@DisplayName("댓글 컨트롤러 테스트")
class CommentControllerTest extends ControllerTest implements CommentTest {

	@DisplayName("댓글 작성 요청 성공")
	@Test
	void create_comment_success() throws Exception {
		// given
		CommentCreateRequestDto requestDto = new CommentCreateRequestDto(TEST_COMMENT_CONTENT_01);
		CommentResponseDto responseDto = new CommentResponseDto(TEST_COMMENT_01);

		given(commentService.createComment(anyLong(), any(CommentCreateRequestDto.class), any(User.class))).willReturn(responseDto);

		String json = objectMapper.writeValueAsString(requestDto);

		// when
		ResultActions resultActions = mvc.perform(post("/api/comments/todocards/{todoCardId}", TODO_CARD_ID_01)
			.content(json)
			.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
			.accept(MediaType.APPLICATION_JSON)
			.principal(mockPrincipal));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value(TEST_COMMENT_CONTENT_01))
			.andExpect(jsonPath("$.username").value(TEST_USER.getUsername()));
	}

	@DisplayName("댓글 작성 요청 실패 - 없는 할일카드 아이디")
	@Test
	void create_comment_fail_not_found_todocard_id() throws Exception {
		// given
		Long todoCardId = 1000L;
		CommentCreateRequestDto requestDto = new CommentCreateRequestDto(TEST_COMMENT_CONTENT_01);

		given(commentService.createComment(anyLong(), any(CommentCreateRequestDto.class), any(User.class))).willThrow(new IllegalArgumentException("잘못된 할일카드 아이디"));

		String json = objectMapper.writeValueAsString(requestDto);

		// when
		ResultActions resultActions = mvc.perform(post("/api/comments/todocards/{todoCardId}", todoCardId)
			.content(json)
			.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
			.accept(MediaType.APPLICATION_JSON)
			.principal(mockPrincipal));

		// then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못된 할일카드 아이디"));
	}

	@DisplayName("댓글 수정 요청 성공")
	@Test
	void edit_comment_success() throws Exception {
		// given
		CommentEditRequestDto requestDto = new CommentEditRequestDto(TEST_COMMENT_CONTENT_01);
		CommentResponseDto responseDto = new CommentResponseDto(TEST_COMMENT_01);

		given(commentService.editComment(anyLong(), any(CommentEditRequestDto.class), any(User.class))).willReturn(responseDto);

		String json = objectMapper.writeValueAsString(requestDto);

		// when
		ResultActions resultActions = mvc.perform(patch("/api/comments/{commentId}", COMMENT_ID_01)
			.content(json)
			.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
			.accept(MediaType.APPLICATION_JSON)
			.principal(mockPrincipal));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value(TEST_COMMENT_CONTENT_01))
			.andExpect(jsonPath("$.username").value(TEST_USER.getUsername()));
	}

	@DisplayName("댓글 수정 요청 실패 - 없는 할일카드 아이디")
	@Test
	void edit_comment_fail_not_found_comment_id() throws Exception {
		// given
		Long todoCardId = 1000L;
		CommentCreateRequestDto requestDto = new CommentCreateRequestDto(TEST_COMMENT_CONTENT_01);

		given(commentService.createComment(anyLong(), any(CommentCreateRequestDto.class), any(User.class))).willThrow(new IllegalArgumentException("잘못된 댓글 아이디"));

		String json = objectMapper.writeValueAsString(requestDto);

		// when
		ResultActions resultActions = mvc.perform(post("/api/comments/todocards/{todoCardId}", todoCardId)
			.content(json)
			.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
			.accept(MediaType.APPLICATION_JSON)
			.principal(mockPrincipal));

		// then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("잘못된 댓글 아이디"));
	}

	@DisplayName("댓글 삭제 요청 성공")
	@Test
	void delete_comment_success() throws Exception {
		// when
		ResultActions resultActions = mvc.perform(delete("/api/comments/{commentId}", COMMENT_ID_01)
			.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
			.accept(MediaType.APPLICATION_JSON)
			.principal(mockPrincipal));

		// then
		resultActions
			.andExpect(status().isOk());
	}
}