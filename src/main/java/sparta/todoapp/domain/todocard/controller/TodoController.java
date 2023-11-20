package sparta.todoapp.domain.todocard.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sparta.todoapp.domain.todocard.dto.request.TodoCardCreateRequestDto;
import sparta.todoapp.domain.todocard.dto.request.TodoCardEditRequestDto;
import sparta.todoapp.domain.todocard.dto.response.TodoCardDetailResponseDto;
import sparta.todoapp.domain.todocard.dto.response.TodoCardSimpleResponseDto;
import sparta.todoapp.domain.todocard.service.TodoCardService;
import sparta.todoapp.global.config.security.CustomUserDetails;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todocards")
public class TodoController {

	private final TodoCardService todoCardService;

	@GetMapping("")
	public ResponseEntity<Map<String, List<TodoCardSimpleResponseDto>>> getTodoCards() {

		Map<String, List<TodoCardSimpleResponseDto>> userTodosMap = todoCardService.getTodoCardsByUsername();

		return ResponseEntity.ok(userTodosMap);
	}

	@GetMapping("/{todoCardId}")
	public ResponseEntity<TodoCardDetailResponseDto> getTodoCard(@PathVariable Long todoCardId) {

		TodoCardDetailResponseDto todoCardDetail = todoCardService.getTodoCard(todoCardId);

		return ResponseEntity.ok(todoCardDetail);
	}

	@PostMapping("")
	public ResponseEntity<TodoCardDetailResponseDto> createTodoCard(
		@RequestBody TodoCardCreateRequestDto requestDto,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		TodoCardDetailResponseDto todoCardDetail = todoCardService.createTodoCard(requestDto, userDetails.getUser());

		return ResponseEntity.ok(todoCardDetail);
	}

	@PatchMapping("/{todoCardId}")
	public ResponseEntity<TodoCardDetailResponseDto> editTodoCard(
		@PathVariable Long todoCardId,
		@RequestBody TodoCardEditRequestDto requestDto,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		TodoCardDetailResponseDto todoCardDetail = todoCardService.editTodoCard(todoCardId, requestDto,
			userDetails.getUsername());

		return ResponseEntity.ok(todoCardDetail);
	}

	@PatchMapping("/{todoCardId}/finish")
	public ResponseEntity<?> finishTodoCard(
		@PathVariable Long todoCardId,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		todoCardService.finishTodoCard(todoCardId, userDetails.getUsername());

		return ResponseEntity.ok().build();
	}
}
