package sparta.todoapp.domain.comment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sparta.todoapp.domain.comment.dto.request.CommentCreateRequestDto;
import sparta.todoapp.domain.comment.dto.request.CommentEditRequestDto;
import sparta.todoapp.domain.comment.dto.response.CommentResponseDto;
import sparta.todoapp.domain.comment.service.CommentService;
import sparta.todoapp.global.config.security.CustomUserDetails;

/**
 * 댓글 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

	private final CommentService commentService;

	/**
	 * 댓글 작성.
	 *
	 * @param todoCardId 어느 할일카드에 달린 댓글인지 체크
	 */
	@PostMapping("/todocards/{todoCardId}")
	public ResponseEntity<CommentResponseDto> createComment(
		@PathVariable Long todoCardId,
		@RequestBody CommentCreateRequestDto requestDto,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		CommentResponseDto responseDto = commentService.createComment(todoCardId, requestDto, userDetails.getUser());

		return ResponseEntity.ok(responseDto);
	}

	/**
	 * 댓글 수정.
	 */
	@PatchMapping("/{commentId}")
	public ResponseEntity<CommentResponseDto> editComment(
		@PathVariable Long commentId,
		@RequestBody CommentEditRequestDto requestDto,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		CommentResponseDto responseDto = commentService.editComment(commentId, requestDto, userDetails.getUser());

		return ResponseEntity.ok(responseDto);
	}

	/**
	 * 댓글 삭제.
	 */
	@DeleteMapping("/{commentId}")
	public ResponseEntity<?> deleteComment(
		@PathVariable Long commentId,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		commentService.deleteComment(commentId, userDetails.getUser());

		return ResponseEntity.ok().build();
	}
}
