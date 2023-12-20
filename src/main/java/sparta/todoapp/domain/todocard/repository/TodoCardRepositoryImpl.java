package sparta.todoapp.domain.todocard.repository;

import static sparta.todoapp.domain.todocard.entity.QTodoCard.todoCard;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sparta.todoapp.domain.todocard.entity.TodoCard;

@Repository
@RequiredArgsConstructor
public class TodoCardRepositoryImpl implements TodoCardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TodoCard> findAllByOrderByCreatedAtDesc() {
        return queryFactory.selectFrom(todoCard)
            .orderBy(todoCard.createdAt.desc())
            .fetch();
    }
}
