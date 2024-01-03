package hexagon.secondary.port;

import java.time.LocalDateTime;
import java.util.Optional;

import hexagon.User;

public interface ForManagingUsers {

	void auditSuccessLogin(String userId, LocalDateTime loginDate);

	Optional<User> userBy(String username, String password);

	void register(User user, String password);

	boolean existsUserBy(String userName);

	// use Optional
	User userById(String id);

	void changePassword(String userId, String newPassword);
}
