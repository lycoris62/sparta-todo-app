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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todocards/{todoCardId}/comment")
public class CommentController {

	private final CommentService commentService;

	@PostMapping
	public ResponseEntity<CommentResponseDto> createComment(
		@PathVariable Long todoCardId,
		@RequestBody CommentCreateRequestDto requestDto,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		CommentResponseDto responseDto = commentService.createComment(todoCardId, requestDto, userDetails.getUser());

		return ResponseEntity.ok(responseDto);
	}

	@PatchMapping("/{commentId}")
	public ResponseEntity<CommentResponseDto> editComment(
		@PathVariable Long commentId,
		@RequestBody CommentEditRequestDto requestDto,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		CommentResponseDto responseDto = commentService.editComment(commentId, requestDto, userDetails.getUser());

		return ResponseEntity.ok(responseDto);
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<?> deleteComment(
		@PathVariable Long commentId,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		commentService.deleteComment(commentId, userDetails.getUser());

		return ResponseEntity.ok().build();
	}
}
