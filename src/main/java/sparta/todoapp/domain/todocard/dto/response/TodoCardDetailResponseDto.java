package sparta.todoapp.domain.todocard.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import sparta.todoapp.domain.comment.dto.response.CommentResponseDto;
import sparta.todoapp.domain.todocard.entity.TodoCard;

/**
 * 할일카드 세부 정보 응답 DTO
 * 할일카드 내의 댓글 리스트도 DTO 반환하여 반환.
 */
@Getter
public class TodoCardDetailResponseDto {

	private final String title;
	private final String content;
	private final String username;
	private final LocalDateTime createdAt;
	private final List<CommentResponseDto> comments;

	public TodoCardDetailResponseDto(TodoCard todoCard) {
		this.title = todoCard.getTitle();
		this.content = todoCard.getContent();
		this.username = todoCard.getAuthor().getUsername();
		this.createdAt = todoCard.getCreatedAt();
		this.comments = todoCard.getCommentList()
			.stream()
			.map(CommentResponseDto::new)
			.toList();
	}
}
