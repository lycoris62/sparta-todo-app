package sparta.todoapp.domain.todocard.controller;

import static java.util.stream.Collectors.*;
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
import java.util.Map;
import java.util.Optional;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import sparta.todoapp.domain.auth.entity.User;
import sparta.todoapp.domain.auth.repository.UserRepository;
import sparta.todoapp.domain.auth.service.AuthService;
import sparta.todoapp.domain.model.BaseEntity;
import sparta.todoapp.domain.todocard.dto.request.TodoCardCreateRequestDto;
import sparta.todoapp.domain.todocard.dto.response.TodoCardDetailResponseDto;
import sparta.todoapp.domain.todocard.dto.response.TodoCardSimpleResponseDto;
import sparta.todoapp.domain.todocard.entity.TodoCard;
import sparta.todoapp.domain.todocard.repository.TodoCardRepository;
import sparta.todoapp.domain.todocard.service.TodoCardService;
import sparta.todoapp.global.config.security.CustomUserDetails;
import sparta.todoapp.global.config.security.MockSpringSecurityFilter;
import sparta.todoapp.global.config.security.WebSecurityConfig;

@DisplayName("할일카드 컨트롤러 테스트")
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(
	controllers = {TodoController.class},
	excludeFilters = {
		@ComponentScan.Filter(
			type = FilterType.ASSIGNABLE_TYPE,
			classes = WebSecurityConfig.class
		)
	}
)
class TodoControllerTest {

	private MockMvc mvc;

	private Principal mockPrincipal;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	TodoCardService todoCardService;

	@MockBean
	AuthService authService;

	@MockBean
	UserRepository userRepository;

	@MockBean
	TodoCardRepository todoCardRepository;

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

		given(todoCardRepository.findAll()).willReturn(List.of(todoCard1, todoCard2, todoCard3));
		given(todoCardRepository.findAllByOrderByCreatedAtDesc()).willReturn(todoCardList);

		CustomUserDetails testUserDetails = new CustomUserDetails(user1);
		mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
	}

	@DisplayName("모든 할일카드를 username 으로 분류하여 응답 성공")
	@Test
	void get_todocards_success() throws Exception {
		// given
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
			.andExpect(jsonPath("$.jaeyun01.size()").value(2))
			.andExpect(jsonPath("$.jaeyun02.size()").value(1))
			.andExpect(jsonPath("$.jaeyun01[0].title").value("title03"))
			.andExpect(jsonPath("$.jaeyun01[0].username").value("jaeyun01"))
			.andExpect(jsonPath("$.jaeyun02[0].title").value("title02"))
			.andExpect(jsonPath("$.jaeyun02[0].username").value("jaeyun02"));
	}

	@DisplayName("할일카드 단건 조회 응답 성공")
	@Test
	void get_todocard_success() throws Exception {
		// given
		Long todoCardId = 1L;
		User user1 = User.createUser("jaeyun01", "12345678");
		TodoCard todoCard1 = new TodoCard("title01", "content01", user1, LocalDateTime.now().minusMinutes(3));

		given(todoCardRepository.findById(todoCardId)).willReturn(Optional.of(todoCard1));
		TodoCardDetailResponseDto responseDto = new TodoCardDetailResponseDto(
			todoCardRepository.findById(todoCardId).get());
		given(todoCardService.getTodoCard(todoCardId)).willReturn(responseDto);

		// when
		ResultActions resultActions = mvc.perform(get("/api/todocards/{todoCardId}", todoCardId)
			.accept(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value(todoCard1.getTitle()))
			.andExpect(jsonPath("$.content").value(todoCard1.getContent()))
			.andExpect(jsonPath("$.username").value(todoCard1.getAuthor().getUsername()))
			// .andExpect(jsonPath("$.createdAt").value(todoCard1.getCreatedAt().toString()))
			.andExpect(jsonPath("$.comments.size()").value(todoCard1.getCommentList().size()));
	}

	@DisplayName("할일카드 단건 조회 응답 실패 - 없는 할일카드")
	@Test
	void get_todocard_fail_not_found() throws Exception {
		// given
		Long todoCardId = 2L;
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
		User user1 = User.createUser("jaeyun01", "12345678");
		TodoCardCreateRequestDto requestDto = new TodoCardCreateRequestDto("title01", "content01", LocalDateTime.now());
		TodoCard todoCard = TodoCard.builder()
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.author(user1)
			.createdAt(requestDto.getCreatedAt())
			.build();

		String json = objectMapper.writeValueAsString(requestDto);
		TodoCardDetailResponseDto responseDto = new TodoCardDetailResponseDto(todoCard);

		given(todoCardService.createTodoCard(any(TodoCardCreateRequestDto.class), any(User.class))).willReturn(
			responseDto);

		// when
		ResultActions resultActions = mvc.perform(post("/api/todocards")
			.content(json)
			.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
			.accept(MediaType.APPLICATION_JSON)
			.principal(mockPrincipal));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value(responseDto.getTitle()))
			.andExpect(jsonPath("$.content").value(responseDto.getContent()))
			.andExpect(jsonPath("$.username").value(responseDto.getUsername()))
			.andExpect(jsonPath("$.comments.size()").value(responseDto.getComments().size()));
	}
}