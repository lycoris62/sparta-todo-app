package sparta.todoapp.domain.todocard.repository;

import java.util.List;
import sparta.todoapp.domain.todocard.entity.TodoCard;

public interface TodoCardRepositoryCustom {
    List<TodoCard> findAllByOrderByCreatedAtDesc();
}
