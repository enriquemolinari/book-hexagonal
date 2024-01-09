package hexagon;

import java.util.UUID;

public class Actor {

    static final String CHARACTER_NAME_INVALID = "character name must not be blank";

    private final UUID id;
    private final Person person;
    private final String characterName;

    public Actor(String id, String name, String surname, String email,
                 String characterName) {
        this.id = UUID.fromString(id);
        this.person = new Person(name, surname, email);
        this.characterName = new NotBlankString(characterName,
                CHARACTER_NAME_INVALID).value();
    }

    public Actor(Person person, String characterName) {
        this.id = UUID.randomUUID();
        this.person = person;
        this.characterName = new NotBlankString(characterName,
                CHARACTER_NAME_INVALID).value();
    }

    public boolean isNamed(String aName) {
        return this.person.isNamed(aName);
    }

    public boolean hasCharacterName(String aCharacterName) {
        return this.characterName.equals(aCharacterName);
    }

    String name() {
        return this.person.name();
    }

    String email() {
        return this.person.email();
    }

    String surname() {
        return this.person.surname();
    }

    String fullName() {
        return this.person.fullName();
    }

    String characterName() {
        return this.characterName;
    }

    String id() {
        return this.id.toString();
    }
}
