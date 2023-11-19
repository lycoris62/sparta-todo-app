package sparta.todoapp.domain.comment.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sparta.todoapp.domain.auth.entity.User;
import sparta.todoapp.domain.comment.dto.request.CommentCreateRequestDto;
import sparta.todoapp.domain.comment.dto.request.CommentEditRequestDto;
import sparta.todoapp.domain.comment.dto.response.CommentResponseDto;
import sparta.todoapp.domain.comment.entity.Comment;
import sparta.todoapp.domain.comment.repository.CommentRepository;
import sparta.todoapp.domain.todocard.entity.TodoCard;
import sparta.todoapp.domain.todocard.repository.TodoCardRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final TodoCardRepository todoCardRepository;

	public CommentResponseDto createComment(Long todoCardId, CommentCreateRequestDto requestDto, User user) {

		TodoCard todoCard = todoCardRepository.findById(todoCardId)
			.orElseThrow(() -> new IllegalArgumentException("잘못된 할일카드 아이디"));

		Comment comment = new Comment(requestDto.getContent(), user, todoCard);
		commentRepository.save(comment);

		return new CommentResponseDto(comment);
	}

	public CommentResponseDto editComment(Long commentId, CommentEditRequestDto requestDto, User user) {

		Comment comment = getRealUserComment(commentId, user);
		comment.update(requestDto);

		return new CommentResponseDto(comment);
	}

	public void deleteComment(Long commentId, User user) {

		Comment comment = getRealUserComment(commentId, user);
		commentRepository.delete(comment);
	}

	private Comment getRealUserComment(Long commentId, User user) {

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException("잘못된 댓글 아이디"));

		accessCheck(user, comment);

		return comment;
	}

	private void accessCheck(User user, Comment comment) {
		if (!user.equals(comment.getAuthor())) {
			throw new AccessDeniedException("다른 사용자의 댓글 접근");
		}
	}
}
