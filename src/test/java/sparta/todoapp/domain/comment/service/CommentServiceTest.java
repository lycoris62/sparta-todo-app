package sparta.todoapp.domain.comment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sparta.todoapp.domain.auth.entity.User;
import sparta.todoapp.domain.auth.repository.UserRepository;
import sparta.todoapp.domain.comment.dto.request.CommentCreateRequestDto;
import sparta.todoapp.domain.comment.dto.request.CommentEditRequestDto;
import sparta.todoapp.domain.comment.dto.response.CommentResponseDto;
import sparta.todoapp.domain.comment.entity.Comment;
import sparta.todoapp.domain.comment.repository.CommentRepository;
import sparta.todoapp.domain.todocard.entity.TodoCard;
import sparta.todoapp.domain.todocard.repository.TodoCardRepository;
import sparta.todoapp.global.error.exception.AccessDeniedException;

@DisplayName("댓글의 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

	@Mock
	CommentRepository commentRepository;
	@Mock
	TodoCardRepository todoCardRepository;

	@InjectMocks
	CommentService commentService;

	@BeforeEach
	void setUp() {
		// given()
	}

	@DisplayName("댓글 작성 성공")
	@Test
	void create_comment_success() {
		// given
		Long todoCardId = 1L;
		User user = User.createUser("jaeyun01", "12345678");
		TodoCard todoCard = new TodoCard("title01", "content01", user, LocalDateTime.now());
		String content = "content01";
		CommentCreateRequestDto requestDto = new CommentCreateRequestDto(content);

		given(todoCardRepository.findById(todoCardId)).willReturn(Optional.of(todoCard));
		Comment comment = new Comment(content, user, todoCard);
		given(commentRepository.save(any(Comment.class))).willReturn(comment);

		// when
		CommentResponseDto responseDto = commentService.createComment(todoCardId, requestDto, user);

		// then
		assertThat(responseDto.getUsername()).isEqualTo(user.getUsername());
		assertThat(responseDto.getContent()).isEqualTo(comment.getContent());
	}

	@DisplayName("댓글 작성 실패 - 잘못된 할일카드 아이디")
	@Test
	void create_comment_fail_not_found_todocardId() {
		// given
		Long todoCardId = 1L;
		User user = User.createUser("jaeyun01", "12345678");
		TodoCard todoCard = new TodoCard("title01", "content01", user, LocalDateTime.now());
		String content = "content01";
		CommentCreateRequestDto requestDto = new CommentCreateRequestDto(content);

		given(todoCardRepository.findById(todoCardId)).willThrow(new IllegalArgumentException("잘못된 할일카드 아이디"));

		// when & when
		assertThatThrownBy(() -> commentService.createComment(todoCardId, requestDto, user))
			.isInstanceOf(IllegalArgumentException.class).hasMessage("잘못된 할일카드 아이디");
	}

	@DisplayName("댓글 수정 성공")
	@Test
	void edit_comment_success() {
		// given
		Long todoCardId = 1L;
		Long commentId = 1L;
		User user = User.createUser("jaeyun01", "12345678");
		TodoCard todoCard = new TodoCard("title01", "content01", user, LocalDateTime.now());
		String content = "content01";
		Comment comment = new Comment(content, user, todoCard);
		CommentEditRequestDto requestDto = new CommentEditRequestDto(content);

		given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

		// when
		CommentResponseDto responseDto = commentService.editComment(commentId, requestDto, user);

		// then
		assertThat(responseDto.getContent()).isEqualTo(content);
		assertThat(responseDto.getUsername()).isEqualTo(user.getUsername());
	}

	@DisplayName("댓글 수정 실패 - 잘못된 댓글 아이디")
	@Test
	void edit_comment_fail_not_found_comment_id() {
		// given
		Long todoCardId = 1L;
		Long commentId = 1L;
		User user = User.createUser("jaeyun01", "12345678");
		TodoCard todoCard = new TodoCard("title01", "content01", user, LocalDateTime.now());
		String content = "content01";
		CommentEditRequestDto requestDto = new CommentEditRequestDto(content);

		given(commentRepository.findById(commentId)).willThrow(new IllegalArgumentException("잘못된 댓글 아이디"));

		// when & when
		assertThatThrownBy(() -> commentService.editComment(todoCardId, requestDto, user))
			.isInstanceOf(IllegalArgumentException.class).hasMessage("잘못된 댓글 아이디");
	}

	@DisplayName("댓글 수정 실패 - 다른 작성자")
	@Test
	void edit_comment_fail_not_same_user() {
		// given
		Long todoCardId = 1L;
		Long commentId = 1L;
		User user1 = User.createUser("jaeyun01", "12345678");
		User user2 = User.createUser("jaeyun02", "12345678");
		TodoCard todoCard = new TodoCard("title01", "content01", user1, LocalDateTime.now());
		String content = "content01";
		Comment comment = new Comment(content, user1, todoCard);
		CommentEditRequestDto requestDto = new CommentEditRequestDto(content);
		given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

		// when & when
		assertThatThrownBy(() -> commentService.editComment(todoCardId, requestDto, user2)).isInstanceOf(AccessDeniedException.class);
	}
}