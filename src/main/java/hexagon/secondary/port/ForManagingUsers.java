package hexagon.secondary.port;

import hexagon.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ForManagingUsers {

    void auditSuccessLogin(String userId, LocalDateTime loginDate);

    Optional<User> userBy(String username, String password);

    void register(User user, String password);

    boolean existsUserBy(String userName);

    Optional<User> userById(String id);

    void changePassword(String userId, String newPassword);
}
