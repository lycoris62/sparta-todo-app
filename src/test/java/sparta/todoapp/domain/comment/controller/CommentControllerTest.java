package sparta.todoapp.domain.comment.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import sparta.todoapp.domain.auth.entity.User;
import sparta.todoapp.domain.comment.dto.request.CommentCreateRequestDto;
import sparta.todoapp.domain.comment.dto.request.CommentEditRequestDto;
import sparta.todoapp.domain.comment.dto.response.CommentResponseDto;
import sparta.todoapp.domain.comment.entity.Comment;
import sparta.todoapp.domain.comment.service.CommentService;
import sparta.todoapp.domain.model.BaseEntity;
import sparta.todoapp.domain.todocard.entity.TodoCard;
import sparta.todoapp.global.config.security.CustomUserDetails;
import sparta.todoapp.global.config.security.MockSpringSecurityFilter;
import sparta.todoapp.global.config.security.WebSecurityConfig;

@ActiveProfiles("test")
@DisplayName("댓글 컨트롤러 테스트")
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(
	controllers = {CommentController.class},
	excludeFilters = {
		@ComponentScan.Filter(
			type = FilterType.ASSIGNABLE_TYPE,
			classes = WebSecurityConfig.class
		)
	}
)
class CommentControllerTest {

	private MockMvc mvc;

	private Principal mockPrincipal;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	CommentService commentService;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(springSecurity(new MockSpringSecurityFilter()))
			.alwaysDo(print())
			.build();

		User user1 = User.createUser("jaeyun01", "12345678");
		User user2 = User.createUser("jaeyun02", "12345678");

		TodoCard todoCard1 = new TodoCard("title01", "content01", user1, LocalDateTime.now().minusMinutes(3));
		TodoCard todoCard2 = new TodoCard("title02", "content02", user2, LocalDateTime.now().minusMinutes(2));
		TodoCard todoCard3 = new TodoCard("title03", "content03", user1, LocalDateTime.now().minusMinutes(1));

		List<TodoCard> todoCardList = new ArrayList<>(List.of(todoCard1, todoCard2, todoCard3));
		todoCardList.sort(Comparator.comparing(BaseEntity::getCreatedAt).reversed());

		CustomUserDetails testUserDetails = new CustomUserDetails(user1);
		mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
	}

	@DisplayName("댓글 작성 요청 성공")
	@Test
	void create_comment_success() throws Exception {
		// given
		Long todoCardId = 1L;
		User user1 = User.createUser("jaeyun01", "12345678");
		TodoCard todoCard1 = new TodoCard("title01", "content01", user1, LocalDateTime.now().minusMinutes(3));
		String content = "content01";
		CommentCreateRequestDto requestDto = new CommentCreateRequestDto(content);
		Comment comment = new Comment(content, user1, todoCard1);
		CommentResponseDto responseDto = new CommentResponseDto(comment);

		given(commentService.createComment(anyLong(), any(CommentCreateRequestDto.class), any(User.class))).willReturn(responseDto);

		String json = objectMapper.writeValueAsString(requestDto);

		// when
		ResultActions resultActions = mvc.perform(post("/api/comments/todocards/{todoCardId}", todoCardId)
			.content(json)
			.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
			.accept(MediaType.APPLICATION_JSON)
			.principal(mockPrincipal));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value(content))
			.andExpect(jsonPath("$.username").value(user1.getUsername()));
	}

	@DisplayName("댓글 작성 요청 실패 - 없는 할일카드 아이디")
	@Test
	void create_comment_fail_not_found_todocard_id() throws Exception {
		// given
		Long todoCardId = 1000L;
		User user1 = User.createUser("jaeyun01", "12345678");
		TodoCard todoCard1 = new TodoCard("title01", "content01", user1, LocalDateTime.now().minusMinutes(3));
		String content = "content01";
		CommentCreateRequestDto requestDto = new CommentCreateRequestDto(content);
		Comment comment = new Comment(content, user1, todoCard1);
		CommentResponseDto responseDto = new CommentResponseDto(comment);

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
		Long commentId = 1L;
		User user1 = User.createUser("jaeyun01", "12345678");
		TodoCard todoCard1 = new TodoCard("title01", "content01", user1, LocalDateTime.now().minusMinutes(3));
		String content = "content01";
		CommentEditRequestDto requestDto = new CommentEditRequestDto(content);
		Comment comment = new Comment(content, user1, todoCard1);
		CommentResponseDto responseDto = new CommentResponseDto(comment);

		given(commentService.editComment(anyLong(), any(CommentEditRequestDto.class), any(User.class))).willReturn(responseDto);

		String json = objectMapper.writeValueAsString(requestDto);

		// when
		ResultActions resultActions = mvc.perform(patch("/api/comments/{commentId}", commentId)
			.content(json)
			.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
			.accept(MediaType.APPLICATION_JSON)
			.principal(mockPrincipal));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value(content))
			.andExpect(jsonPath("$.username").value(user1.getUsername()));
	}

	@DisplayName("댓글 수정 요청 실패 - 없는 할일카드 아이디")
	@Test
	void edit_comment_fail_not_found_comment_id() throws Exception {
		// given
		Long todoCardId = 1000L;
		User user1 = User.createUser("jaeyun01", "12345678");
		TodoCard todoCard1 = new TodoCard("title01", "content01", user1, LocalDateTime.now().minusMinutes(3));
		String content = "content01";
		CommentCreateRequestDto requestDto = new CommentCreateRequestDto(content);
		Comment comment = new Comment(content, user1, todoCard1);
		CommentResponseDto responseDto = new CommentResponseDto(comment);

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
		// given
		Long commentId = 1L;

		// when
		ResultActions resultActions = mvc.perform(delete("/api/comments/{commentId}", commentId)
			.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
			.accept(MediaType.APPLICATION_JSON)
			.principal(mockPrincipal));

		// then
		resultActions
			.andExpect(status().isOk());
	}
}