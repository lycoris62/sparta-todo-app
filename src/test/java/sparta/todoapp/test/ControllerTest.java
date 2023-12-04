package sparta.todoapp.test;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.security.Principal;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import sparta.todoapp.domain.auth.controller.AuthController;
import sparta.todoapp.domain.auth.repository.UserRepository;
import sparta.todoapp.domain.auth.service.AuthService;
import sparta.todoapp.domain.comment.controller.CommentController;
import sparta.todoapp.domain.comment.service.CommentService;
import sparta.todoapp.domain.todocard.controller.TodoController;
import sparta.todoapp.domain.todocard.repository.TodoCardRepository;
import sparta.todoapp.domain.todocard.service.TodoCardService;
import sparta.todoapp.global.config.security.CustomUserDetails;
import sparta.todoapp.global.config.security.MockSpringSecurityFilter;
import sparta.todoapp.global.config.security.WebSecurityConfig;

@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(
	controllers = {
		AuthController.class,
		TodoController.class,
		CommentController.class
	},
	excludeFilters = {
		@ComponentScan.Filter(
			type = FilterType.ASSIGNABLE_TYPE,
			classes = WebSecurityConfig.class
		)
	}
)
public abstract class ControllerTest implements UserTest { // 그냥 클래스로 하면 테스트의 대상이 되어 추상 클래스.

	@Autowired
	private WebApplicationContext context;

	@Autowired
	protected MockMvc mvc;

	@Autowired
	protected ObjectMapper objectMapper;

	protected Principal mockPrincipal;

	@MockBean
	protected AuthService authService;

	@MockBean
	protected CommentService commentService;

	@MockBean
	protected TodoCardService todoCardService;

	@MockBean
	protected UserRepository userRepository;

	@MockBean
	protected TodoCardRepository todoCardRepository;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(springSecurity(new MockSpringSecurityFilter()))
			.alwaysDo(print())
			.build();

		// Mock 테스트 UserDetails 생성
		CustomUserDetails userDetails = new CustomUserDetails(TEST_USER);

		// SecurityContext 에 인증된 사용자 설정
		var authentication = new UsernamePasswordAuthenticationToken(
			userDetails, userDetails.getPassword(), userDetails.getAuthorities());
		this.mockPrincipal = authentication;

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
