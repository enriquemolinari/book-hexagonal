package infra.secondary.jpa;

import hexagon.User;
import hexagon.secondary.port.ForManagingUsers;
import infra.secondary.jpa.entities.LoginAuditEntity;
import infra.secondary.jpa.entities.UserEntity;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class JpaForManagingUsers implements ForManagingUsers {
    private final EntityManager em;

    public JpaForManagingUsers(EntityManager em) {
        this.em = em;
    }

    @Override
    public boolean existsUserBy(String userName) {
        var q = this.em.createQuery(
                "select u from UserEntity u where u.userName = ?1 ",
                UserEntity.class);
        q.setParameter(1, userName);
        var mightBeAUser = q.getResultList();
        return !mightBeAUser.isEmpty();
    }

    @Override
    public void register(User user, String password) {
        var userEntity = UserEntity.fromDomainWithPassword(user, password);
        em.persist(userEntity);
    }

    @Override
    public User userById(String id) {
        return em.find(UserEntity.class, UUID.fromString(id)).toDomain();
    }

    @Override
    public void changePassword(String userId, String newPassword) {
        var userEntity = em.getReference(UserEntity.class, UUID.fromString(userId));
        userEntity.newPassword(newPassword);
    }

    @Override
    public void auditSuccessLogin(String userId, LocalDateTime loginDate) {
        var user = em.getReference(UserEntity.class, UUID.fromString(userId));
        em.persist(new LoginAuditEntity(loginDate, user));
    }

    @Override
    public Optional<User> userBy(String username, String password) {
        var q = this.em.createQuery(
                "select u from UserEntity u where u.userName = ?1 and u.password = ?2",
                UserEntity.class);
        q.setParameter(1, username);
        q.setParameter(2, password);
        var users = q.getResultList();
        if (!users.isEmpty()) {
            return Optional.of(users.get(0).toDomain());
        }
        return Optional.empty();
    }
}
