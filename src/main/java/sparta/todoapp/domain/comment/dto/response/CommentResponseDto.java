package sparta.todoapp.domain.comment.dto.response;

import lombok.Getter;
import sparta.todoapp.domain.comment.entity.Comment;

@Getter
public class CommentResponseDto {

	private final Long commentId;
	private final String content;
	private final String username;

	public CommentResponseDto(Comment comment) {
		this.commentId = comment.getId();
		this.content = comment.getContent();
		this.username = comment.getAuthor().getUsername();
	}
}
