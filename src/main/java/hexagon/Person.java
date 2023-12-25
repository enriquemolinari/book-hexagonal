package hexagon;

import java.util.UUID;

public class Person {

	static final String NAME_MUST_NOT_BE_BLANK = "Name must not be blank";
	static final String SURNAME_MUST_NOT_BE_BLANK = "Surname must not be blank";

	private UUID id;
	private String name;
	private String surname;
	private Email email;

	public Person(String name, String surname, String email) {
		this.id = UUID.randomUUID();
		this.name = new NotBlankString(name, NAME_MUST_NOT_BE_BLANK).value();
		this.surname = new NotBlankString(surname, SURNAME_MUST_NOT_BE_BLANK)
				.value();
		this.email = new Email(email);
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

	String email() {
		return this.email.asString();
	}

	String name() {
		return this.name;
	}

	String surname() {
		return this.surname;
	}

	String id() {
		return this.id.toString();
	}
}
