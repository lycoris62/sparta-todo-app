package sparta.todoapp.domain.todocard.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import sparta.todoapp.domain.todocard.entity.TodoCard;

@Getter
public class TodoCardDetailResponseDto {

	private final String title;
	private final String content;
	private final String username;
	private final LocalDateTime createdAt;

	public TodoCardDetailResponseDto(TodoCard todoCard) {
		this.title = todoCard.getTitle();
		this.content = todoCard.getContent();
		this.username = todoCard.getAuthor().getUsername();
		this.createdAt = todoCard.getCreatedAt();
	}
}
