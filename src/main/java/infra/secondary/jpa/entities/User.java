package infra.secondary.jpa.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import hexagon.primary.port.BusinessException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ClientUser")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"userName"})
public class User {

	@Id
	private UUID id;
	@Column(unique = true)
	private String userName;
	private String name;
	private String surname;
	private String email;
	private String password;

	@OneToMany(cascade = CascadeType.PERSIST, mappedBy = "purchaser")
	private List<Sale> purchases;

	private int points;

	public User(String id, String name, String surname,
			String email, String userName, String password,
			String repeatPassword) {
		this.id = UUID.fromString(id);
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.userName = userName;
		this.password = password;
		this.points = 0;
		this.purchases = new ArrayList<>();
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

	void newPurchase(Sale sale, int pointsWon) {
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

	// public UserProfile toProfile() {
	// return new UserProfile(this.person.fullName(), this.userName,
	// this.person.email(), this.points);
	// }
}
