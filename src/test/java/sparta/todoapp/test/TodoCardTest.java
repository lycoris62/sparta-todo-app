package sparta.todoapp.test;

import java.time.LocalDateTime;

import sparta.todoapp.domain.todocard.entity.TodoCard;

public interface TodoCardTest extends UserTest {

	Long TODO_CARD_ID_01 = 1L;
	Long TODO_CARD_ID_02 = 2L;
	Long TODO_CARD_ID_03 = 3L;

	String TEST_TODO_CARD_TITLE_01 = "title01";
	String TEST_TODO_CARD_TITLE_02 = "title02";
	String TEST_TODO_CARD_TITLE_03 = "title03";

	String TEST_TODO_CARD_CONTENT_01 = "content01";
	String TEST_TODO_CARD_CONTENT_02 = "content02";
	String TEST_TODO_CARD_CONTENT_03 = "content03";

	LocalDateTime NOW = LocalDateTime.now();

	TodoCard TEST_TODO_CARD_01 = new TodoCard(TEST_TODO_CARD_TITLE_01, TEST_TODO_CARD_CONTENT_01, TEST_USER, NOW.minusMinutes(3));
	TodoCard TEST_TODO_CARD_02 = new TodoCard(TEST_TODO_CARD_TITLE_02, TEST_TODO_CARD_CONTENT_02, TEST_ANOTHER_USER, NOW.minusMinutes(2));
	TodoCard TEST_TODO_CARD_03 = new TodoCard(TEST_TODO_CARD_TITLE_03, TEST_TODO_CARD_CONTENT_03, TEST_USER, NOW.minusMinutes(1));
}
