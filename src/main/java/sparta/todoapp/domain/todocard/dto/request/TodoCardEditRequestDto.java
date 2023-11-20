package sparta.todoapp.domain.todocard.dto.request;

import lombok.Getter;

/**
 * 할일카드 수정 요청 DTO
 */
@Getter
public class TodoCardEditRequestDto {

	private String title;
	private String content;
}
