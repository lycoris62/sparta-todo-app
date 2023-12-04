package sparta.todoapp.test;

import sparta.todoapp.domain.comment.entity.Comment;

public interface CommentTest extends TodoCardTest {

	Long COMMENT_ID_01 = 1L;
	Long COMMENT_ID_02 = 1L;
	Long COMMENT_ID_03 = 1L;

	String TEST_COMMENT_CONTENT_01 = "content01";
	String TEST_COMMENT_CONTENT_02 = "content02";
	String TEST_COMMENT_CONTENT_03 = "content03";
	String TEST_COMMENT_CONTENT_04 = "content04";

	Comment TEST_COMMENT_01 = new Comment(TEST_COMMENT_CONTENT_01, TEST_USER, TEST_TODO_CARD_01);
	Comment TEST_COMMENT_02 = new Comment(TEST_COMMENT_CONTENT_02, TEST_USER, TEST_TODO_CARD_02);
	Comment TEST_COMMENT_03 = new Comment(TEST_COMMENT_CONTENT_03, TEST_ANOTHER_USER, TEST_TODO_CARD_01);
	Comment TEST_COMMENT_04 = new Comment(TEST_COMMENT_CONTENT_04, TEST_USER, TEST_TODO_CARD_03);
}
