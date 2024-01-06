package infra.secondary.inmemory;

import hexagon.User;
import hexagon.secondary.port.ForManagingUsers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HashMapForManagingUsers implements ForManagingUsers {
    private Map<String, User> usersById;
    private Map<String, User> usersByUserName;
    private Map<String, String> passwords;

    public HashMapForManagingUsers() {
        this.usersById = new HashMap<>();
        this.passwords = new HashMap<>();
        this.usersByUserName = new HashMap<>();
    }

    @Override
    public void auditSuccessLogin(String userId, LocalDateTime loginDate) {
        //just do nothing...
    }

    @Override
    public Optional<User> userBy(String username, String password) {
        var user = usersByUserName.get(username);
        if (user.hasPassword(password)) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public void register(User user, String password) {
        usersById.put(user.id(), user);
        passwords.put(user.id(), password);
        usersByUserName.put(user.getUserName(), user);
    }

    @Override
    public boolean existsUserBy(String userName) {
        return usersByUserName.containsKey(userName);
    }

    @Override
    public User userById(String id) {
        return usersById.get(id);
    }

    @Override
    public void changePassword(String userId, String newPassword) {
        passwords.replace(userId, newPassword);
    }
}
