package sparta.todoapp.domain.todocard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.todoapp.domain.todocard.entity.TodoCard;

public interface TodoCardRepository extends JpaRepository<TodoCard, Long>, TodoCardRepositoryCustom {

    default TodoCard getTodoCardById(Long todoCardId) {
        return findById(todoCardId).orElseThrow(() -> new IllegalArgumentException("잘못된 아이디"));
    }
}
