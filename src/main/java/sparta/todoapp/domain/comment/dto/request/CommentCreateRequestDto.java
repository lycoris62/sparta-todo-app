package sparta.todoapp.domain.comment.dto.request;

import lombok.Getter;

/**
 * 댓글 작성 요청 DTO
 */
@Getter
public class CommentCreateRequestDto {

	private String content;
}
