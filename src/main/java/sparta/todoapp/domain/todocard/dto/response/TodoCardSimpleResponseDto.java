package sparta.todoapp.domain.todocard.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import sparta.todoapp.domain.todocard.entity.TodoCard;

@Getter
public class TodoCardSimpleResponseDto {

	private final String title;
	private final String username;
	private final LocalDateTime createdAt;
	private final boolean isDone;

	public TodoCardSimpleResponseDto(TodoCard todoCard) {
		this.title = todoCard.getTitle();
		this.username = todoCard.getAuthor().getUsername();
		this.createdAt = todoCard.getCreatedAt();
		this.isDone = todoCard.isDone();
	}
}
