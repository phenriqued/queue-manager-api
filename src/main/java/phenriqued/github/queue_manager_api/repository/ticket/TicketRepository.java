package phenriqued.github.queue_manager_api.repository.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {

    @Query("SELECT t FROM TicketEntity t " +
            "LEFT JOIN FETCH t.owner " +
            "WHERE t.id = :id")
    Optional<TicketEntity> findByIdWithCustomer(@Param("id") Long id);

}
