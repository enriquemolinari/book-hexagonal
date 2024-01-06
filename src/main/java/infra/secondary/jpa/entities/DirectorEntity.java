package infra.secondary.jpa.entities;

import hexagon.Person;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

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

    Person toDomain() {
        return new Person(this.name, this.surname, this.email);
    }
}
