package sparta.todoapp.domain.comment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.todoapp.domain.auth.entity.User;
import sparta.todoapp.domain.comment.dto.request.CommentEditRequestDto;
import sparta.todoapp.domain.model.BaseEntity;
import sparta.todoapp.domain.todocard.entity.TodoCard;

@Getter
@Entity
@Table(name = "comments") // comment 는 예약어 이므로 s를 붙임
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 최소 범위 접근 제어자
public class Comment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(columnDefinition = "TEXT")
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User author;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "todocard_id")
	private TodoCard todoCard;

	public Comment(String content, User author, TodoCard todoCard) {
		this.content = content;
		this.author = author;
		this.todoCard = todoCard;
	}

	public void update(CommentEditRequestDto requestDto) {
		this.content = requestDto.getContent();
	}
}
