package sparta.todoapp.domain.todocard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sparta.todoapp.domain.todocard.entity.TodoCard;

public interface TodoCardRepository extends JpaRepository<TodoCard, Long> {
	List<TodoCard> findAllByOrderByCreatedAtDesc(); // 생성일자를 기준으로 내림차순한 할일카드 리스트 조회
}
