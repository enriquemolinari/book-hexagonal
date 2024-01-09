package infra.secondary.inmemory;

import hexagon.User;
import hexagon.secondary.port.ForManagingUsers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HashMapForManagingUsers implements ForManagingUsers {
    private final Map<String, User> usersById;
    private final Map<String, User> usersByUserName;

    public HashMapForManagingUsers() {
        this.usersById = new HashMap<>();
        this.usersByUserName = new HashMap<>();
    }

    @Override
    public void auditSuccessLogin(String userId, LocalDateTime loginDate) {
        //not required
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
        usersByUserName.put(user.getUserName(), user);
    }

    @Override
    public boolean existsUserBy(String userName) {
        return usersByUserName.containsKey(userName);
    }

    @Override
    public Optional<User> userById(String id) {
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public void changePassword(String userId, String newPassword) {
        //Implementation not requiered as I'm storing the User instance
        //And returned as a reference. changePassword method is invoked
        //on the hexagon and the stored instance updated right the way
    }
}
