package infra.secondary.jpa;

import hexagon.Theater;
import hexagon.secondary.port.ForManagingShows;
import infra.secondary.jpa.entities.TheaterEntity;
import jakarta.persistence.EntityManager;

public class JpaForManagingShows implements ForManagingShows {
	private EntityManager em;

	public JpaForManagingShows(EntityManager em) {
		this.em = em;
	}

	@Override
	public void addTheater(Theater theater) {
		var t = new TheaterEntity(theater.id(), theater.name(),
				theater.seats());
		em.persist(t);
	}

}
