package sparta.todoapp.domain.todocard.dto.request;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class TodoCardCreateRequestDto {

	private String title;
	private String content;
	private LocalDateTime createdAt;
}
