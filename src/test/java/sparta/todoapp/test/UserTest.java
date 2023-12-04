package sparta.todoapp.test;

import sparta.todoapp.domain.auth.entity.User;

public interface UserTest {

	Long TEST_USER_ID = 1L;
	Long ANOTHER_TEST_USER_ID = 2L;

	String TEST_USER_NAME = "username";
	String TEST_USER_PASSWORD = "12345678";

	String TEST_ANOTHER_USER_NAME = "another-";
	String TEST_ANOTHER_USER_PASSWORD = "12345678";

	User TEST_USER = User.createUser(TEST_USER_NAME, TEST_USER_PASSWORD);
	User TEST_ANOTHER_USER = User.createUser(TEST_ANOTHER_USER_NAME, TEST_ANOTHER_USER_PASSWORD);
}
