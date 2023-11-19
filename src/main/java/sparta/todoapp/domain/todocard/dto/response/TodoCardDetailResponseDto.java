package sparta.todoapp.domain.todocard.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import sparta.todoapp.domain.comment.dto.response.CommentResponseDto;
import sparta.todoapp.domain.todocard.entity.TodoCard;

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
