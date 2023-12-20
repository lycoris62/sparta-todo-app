package sparta.todoapp.test;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import sparta.todoapp.domain.todocard.repository.TodoCardRepositoryImpl;

@TestConfiguration
public class TestQueryDslConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public TodoCardRepositoryImpl todoCardRepositoryImpl() {
        return new TodoCardRepositoryImpl(jpaQueryFactory());
    }
}
