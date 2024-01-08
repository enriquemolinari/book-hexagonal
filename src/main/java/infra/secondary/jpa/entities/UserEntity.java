package infra.secondary.jpa.entities;

import hexagon.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "clientuser")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"userName"})
public class UserEntity {

    @Id
    private UUID id;
    @Column(unique = true)
    private String userName;
    private String name;
    private String surname;
    private String email;
    private String password;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "purchaser")
    private List<SaleEntity> purchases;
    @Setter
    private int points;

    public static UserEntity fromDomainWithPassword(User user, String password) {
        return new UserEntity(user.id(), user.name(), user.surname(),
                user.email(), user.getUserName(), password);
    }

    public static UserEntity fromDomain(User user) {
        return new UserEntity(user.id(), user.name(), user.surname(),
                user.email(), user.getUserName(), null /*
         * password not changed from
         * here
         */);
    }

    public UserEntity(String id, String name, String surname,
                      String email, String userName, String password) {
        this.id = UUID.fromString(id);
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.points = 0;
        this.purchases = new ArrayList<>();
    }

    UserEntity(UUID id) {
        this.id = id;
    }

    public static UserEntity fromId(String id) {
        return new UserEntity(UUID.fromString(id));
    }

    public String userName() {
        return userName;
    }

    public void addPurchase(SaleEntity sale) {
        this.purchases.add(sale);
    }

    String email() {
        return this.email;
    }

    public Map<String, Object> toMap() {
        return Map.of("id", this.id);
    }

    String id() {
        return id.toString();
    }

    public User toDomain() {
        return new User(this.id.toString(), this.name, this.surname, this.email,
                this.userName, this.password);
    }

    public void newPassword(String newPassword) {
        this.password = newPassword;
    }
}
