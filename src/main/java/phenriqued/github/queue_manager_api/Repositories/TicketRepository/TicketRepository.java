package phenriqued.github.queue_manager_api.Repositories.TicketRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import phenriqued.github.queue_manager_api.Models.Ticket.TicketEntity;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {

    @Query("SELECT ticket FROM TicketEntity ticket " +
            "JOIN FETCH ticket.owner " +
            "WHERE ticket.id = :id")
    Optional<TicketEntity> findByIdWithCustomer(Long id);

}
