package infra.secondary.jpa;

import hexagon.secondary.port.ForManagingUsers;
import jakarta.persistence.EntityManager;

public class JpaForManagingUsers implements ForManagingUsers {
	private EntityManager em;

	public JpaForManagingUsers(EntityManager em) {
		this.em = em;
	}

}
