package infra.secondary.jpa.entities;

import hexagon.Actor;
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
public class ActorEntity {

    @Id
    private UUID id;
    private String name;
    private String surname;
    private String email;
    private String characterName;

    public ActorEntity(String id, String name, String surname, String email,
                       String characterName) {
        this.id = UUID.fromString(id);
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.characterName = characterName;
    }

    public Actor toDomain() {
        return new Actor(this.id.toString(), this.name, this.surname,
                this.email,
                this.characterName);
    }
}
