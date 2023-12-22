package sparta.todoapp.domain.todocard.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sparta.todoapp.domain.todocard.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {

	@Query("SELECT COUNT(*) FROM Like l WHERE l.todoCard.id = :todoCardId")
	Long countLikeByTodoCard_Id(Long todoCardId);

	Optional<Like> findByUser_IdAndTodoCard_Id(Long userId, Long todoCardId);
}
