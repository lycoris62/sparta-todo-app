package sparta.todoapp.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sparta.todoapp.domain.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
