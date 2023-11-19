package sparta.todoapp.domain.todocard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sparta.todoapp.domain.todocard.entity.TodoCard;

public interface TodoCardRepository extends JpaRepository<TodoCard, Long> {
	List<TodoCard> findAllByOrderByCreatedAtDesc();
}
