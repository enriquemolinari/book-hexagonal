package infra.secondary.jpa.entities;

import java.util.UUID;

import hexagon.Person;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
public class DirectorEntity {

	@Id
	private UUID id;
	private String name;
	private String surname;
	private String email;

	public DirectorEntity(String id, String name, String surname,
			String email) {
		this.id = UUID.fromString(id);
		this.name = name;
		this.surname = surname;
		this.email = email;
	}

	public boolean isNamed(String aName) {
		return this.fullName().equals(aName);
	}

	String fullName() {
		return this.name + " " + this.surname;
	}

	public boolean hasName(String aName) {
		return this.name.equals(aName);
	}

	public boolean aSurname(String aSurname) {
		return this.surname.equals(aSurname);
	}

	Person toDomain() {
		return new Person(this.name, this.surname, this.email);
	}

	// public PersonRequest toPersonRequest() {
	// return new PersonRequest(this.id.toString(), this.name, this.surname,
	// this.email);
	// }
}
