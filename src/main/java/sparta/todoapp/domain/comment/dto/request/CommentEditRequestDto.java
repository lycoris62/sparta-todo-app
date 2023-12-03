package sparta.todoapp.domain.comment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 댓글 수정 요청 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentEditRequestDto {

	private String content;
}
