package infra.secondary.jpa.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "loginaudit")
@Getter(value = AccessLevel.PRIVATE)
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor
public class LoginAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDateTime loginDateTime;
    @ManyToOne
    private UserEntity user;

    public LoginAuditEntity(LocalDateTime loginDateTime, UserEntity user) {
        this.loginDateTime = loginDateTime;
        this.user = user;
    }
}
