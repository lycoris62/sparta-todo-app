package sparta.todoapp.domain.comment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sparta.todoapp.domain.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	@Query("SELECT C FROM Comment C WHERE C.todoCard.id = :todoCardId")
	List<Comment> findAllByTodoCard_Id(Long todoCardId);
}
