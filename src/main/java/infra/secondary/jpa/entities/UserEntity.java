package infra.secondary.jpa.entities;

import hexagon.User;
import hexagon.primary.port.BusinessException;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "ClientUser")
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

    private int points;

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

    // TODO: ver que hacer con esto...
    void newEarnedPoints(int points) {
        if (points <= 0) {
            throw new BusinessException("points must be greater than zero");
        }
        this.points += points;
    }

    public boolean hasPoints(int points) {
        return this.points == points;
    }

    public String userName() {
        return userName;
    }

    public boolean hasName(String aName) {
        return this.name.equals(aName);
    }

    public boolean hasSurname(String aSurname) {
        return this.surname.equals(aSurname);
    }

    public boolean hasUsername(String aUserName) {
        return this.userName.equals(aUserName);
    }

    void newPurchase(SaleEntity sale, int pointsWon) {
        this.newEarnedPoints(pointsWon);
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

    public static UserEntity fromDomain(User user) {
        return new UserEntity(user.id(), user.name(), user.surname(),
                user.email(), user.userName(), null /*
         * password not changed from
         * here
         */);
    }
}
