package sparta.todoapp.domain.todocard.entity;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import sparta.todoapp.domain.auth.entity.User;
import sparta.todoapp.domain.comment.dto.response.CommentResponseDto;
import sparta.todoapp.domain.comment.entity.Comment;
import sparta.todoapp.domain.model.BaseEntity;
import sparta.todoapp.domain.todocard.dto.request.TodoCardEditRequestDto;

/**
 * 할일카드 엔티티
 */
@Getter
@Entity
@Table(name = "todocard") // 테이블명 명시
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 엔티티의 최소 접근제어자로 설정
public class TodoCard extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 500, nullable = false)
	private String title;

	@Column(length = 5000, nullable = false)
	private String content;

	@Column
	private boolean isDone = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User author;

	@BatchSize(size = 100)
	@OneToMany(mappedBy = "todoCard", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> commentList = new ArrayList<>();

	@OneToMany(mappedBy = "todoCard", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Like> likeList = new ArrayList<>();

	@Transient
	private Long likeCount;

	@Transient
	private List<CommentResponseDto> commentResponseList = new ArrayList<>();

	@Builder
	public TodoCard(String title, String content, User author, LocalDateTime createdAt) {
		this.title = title;
		this.content = content;
		this.author = author;
		this.isDone = false;

		if (createdAt != null) { // 생성일자를 입력하면 그 생성일자로 저장
			this.createdAt = createdAt;
		}
	}

	/**
	 * 할일카드 수정
	 */
	public void update(TodoCardEditRequestDto requestDto) {
		this.title = requestDto.getTitle();
		this.content = requestDto.getContent();
	}

	/**
	 * 할일카드 완료
	 */
	public void finish() {
		this.isDone = true;
	}

	/**
	 * 좋아요 추가
	 */
	public void like(User user) {
		Like like = Like.builder()
			.user(user)
			.todoCard(this)
			.build();

		likeList.add(like);
	}

	public void setLikeCount(long likeCount) {
		this.likeCount = likeCount;
	}

	public void setCommentResponseList(List<CommentResponseDto> commentResponseList) {
		this.commentResponseList = commentResponseList;
	}
}
