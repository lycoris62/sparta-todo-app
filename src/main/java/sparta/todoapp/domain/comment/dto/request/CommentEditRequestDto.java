package sparta.todoapp.domain.comment.dto.request;

import lombok.Getter;

/**
 * 댓글 수정 요청 DTO
 */
@Getter
public class CommentEditRequestDto {

	private String content;
}
