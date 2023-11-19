package sparta.todoapp.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;

/**
 * 모든 엔티티가 가져야 할 속성을 가지는 엔티티.
 * 생성일자와 수정일자를 저장하며, 상속으로 사용 가능.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	@CreatedDate
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	protected LocalDateTime createdAt; // 생성일자

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	protected LocalDateTime updatedAt; // 수정일자
}
