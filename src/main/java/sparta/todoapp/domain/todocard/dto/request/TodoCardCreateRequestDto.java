package sparta.todoapp.domain.todocard.dto.request;

import java.time.LocalDateTime;

import lombok.Getter;

/**
 * 할일카드 생성 요청 DTO
 */
@Getter
public class TodoCardCreateRequestDto {

	private String title;
	private String content;
	private LocalDateTime createdAt;
}
