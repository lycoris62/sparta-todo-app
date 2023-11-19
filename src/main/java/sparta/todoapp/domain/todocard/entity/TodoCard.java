package sparta.todoapp.domain.todocard.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.todoapp.domain.auth.entity.User;
import sparta.todoapp.domain.comment.entity.Comment;
import sparta.todoapp.domain.model.BaseEntity;
import sparta.todoapp.domain.todocard.dto.request.TodoCardEditRequestDto;

@Getter
@Entity
@Table(name = "todocard")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoCard extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 30, nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;

	@Column
	private boolean isDone = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User author;

	@OneToMany(mappedBy = "todoCard")
	private List<Comment> commentList = new ArrayList<>();

	@Builder
	public TodoCard(String title, String content, User author, LocalDateTime createdAt) {
		this.title = title;
		this.content = content;
		this.author = author;
		this.isDone = false;

		if (createdAt != null) {
			this.createdAt = createdAt;
		}
	}

	public void update(TodoCardEditRequestDto requestDto) {
		this.title = requestDto.getTitle();
		this.content = requestDto.getContent();
	}

	public void finish() {
		this.isDone = true;
	}
}
