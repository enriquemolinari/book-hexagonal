package hexagon;

import hexagon.primary.port.BusinessException;
import hexagon.primary.port.UserProfile;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@EqualsAndHashCode(of = {"userName"})
public class User {

    static final String INVALID_USERNAME = "A valid username must be provided";
    static final String CAN_NOT_CHANGE_PASSWORD = "Some of the provided information is not valid to change the password";
    static final String POINTS_MUST_BE_GREATER_THAN_ZERO = "Points must be greater than zero";
    static final String PASSWORDS_MUST_BE_EQUALS = "Passwords must be equals";

    @Id
    private UUID id;
    @Getter
    private final String userName;
    private final Person person;
    // password must not escape by any means out of this object
    // DO NOT expose getters
    private Password password;
    private List<Sale> purchases;
    @Getter
    private int points;

    public User(String id, String name, String surname, String email,
                String userName, String password) {
        this(id, new Person(name, surname, email), userName, password,
                password);
    }

    User(Person person,
         String userName, String password,
         String repeatPassword) {
        this(UUID.randomUUID().toString(), person, userName, password,
                repeatPassword);
    }

    User(String id, Person person, String userName, String password,
         String repeatPassword) {
        checkPasswordsMatch(password, repeatPassword);
        this.id = UUID.fromString(id);
        this.person = person;
        this.userName = new NotBlankString(userName,
                INVALID_USERNAME).value();
        this.password = new Password(password);
        this.points = 0;
        this.purchases = new ArrayList<>();
    }

    private void checkPasswordsMatch(String password, String repeatPassword) {
        if (!password.equals(repeatPassword)) {
            throw new BusinessException(PASSWORDS_MUST_BE_EQUALS);
        }
    }

    public boolean hasPassword(String password) {
        return this.password.equals(new Password(password));
    }

    public void changePassword(String currentPassword, String newPassword1,
                               String newPassword2) {
        if (!hasPassword(currentPassword)) {
            throw new BusinessException(CAN_NOT_CHANGE_PASSWORD);
        }
        checkPasswordsMatch(newPassword2, newPassword1);

        this.password = new Password(newPassword1);
    }

    void newEarnedPoints(int points) {
        if (points <= 0) {
            throw new BusinessException(POINTS_MUST_BE_GREATER_THAN_ZERO);
        }
        this.points += points;
    }

    public boolean hasPoints(int points) {
        return this.points == points;
    }

    public boolean hasName(String aName) {
        return this.person.hasName(aName);
    }

    public boolean hasSurname(String aSurname) {
        return this.person.aSurname(aSurname);
    }

    public boolean hasUsername(String aUserName) {
        return this.userName.equals(aUserName);
    }

    void newPurchase(Sale sale, int pointsWon) {
        this.newEarnedPoints(pointsWon);
        this.purchases.add(sale);
    }

    public String email() {
        return this.person.email();
    }

    public Map<String, Object> toMap() {
        return Map.of("id", this.id);
    }

    public String id() {
        return id.toString();
    }

    public String name() {
        return this.person.name();
    }

    public String surname() {
        return this.person.surname();
    }

    public UserProfile toProfile() {
        return new UserProfile(this.person.fullName(), this.userName,
                this.person.email(), this.points);
    }
}
