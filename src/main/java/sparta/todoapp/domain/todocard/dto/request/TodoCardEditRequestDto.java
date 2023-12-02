package sparta.todoapp.domain.todocard.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 할일카드 수정 요청 DTO
 */
@Getter
@AllArgsConstructor
public class TodoCardEditRequestDto {

	private String title;
	private String content;
}
