package hexagon;

import java.time.LocalDateTime;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

//@Entity
//@Table
@Getter(value = AccessLevel.PRIVATE)
@Setter(value = AccessLevel.PRIVATE)
// @NoArgsConstructor
class LoginAudit {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private LocalDateTime loginDateTime;
	@ManyToOne
	private User user;

	public LoginAudit(LocalDateTime loginDateTime, User user) {
		this.loginDateTime = loginDateTime;
		this.user = user;
	}
}
