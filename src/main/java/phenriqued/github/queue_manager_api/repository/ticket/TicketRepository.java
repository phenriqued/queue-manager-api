package phenriqued.github.queue_manager_api.repository.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketStatus;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {

    @Query("SELECT t FROM TicketEntity t " +
            "LEFT JOIN FETCH t.owner " +
            "WHERE t.id = :id")
    Optional<TicketEntity> findByIdWithCustomer(@Param("id") Long id);

    @Query("SELECT t FROM TicketEntity t " +
            "WHERE t.queue.id = :queueId AND " +
            "t.status = :status")
    List<TicketEntity> findAllByQueueIdAndStatus(@Param("queueId") Long queueId, @Param("status") TicketStatus status);

    @Query("SELECT t FROM TicketEntity t " +
            "WHERE t.queue.id = :queueId")
    List<TicketEntity> findAllByQueueId(@Param("queueId") Long queueId);

    @Query("SELECT COUNT(t) FROM TicketEntity t " +
            "WHERE t.queue.id = :queueId AND " +
            "t.status = :status")
    long countByQueueIdAndStatus(@Param("queueId") Long queueId, @Param("status") TicketStatus status);



}
