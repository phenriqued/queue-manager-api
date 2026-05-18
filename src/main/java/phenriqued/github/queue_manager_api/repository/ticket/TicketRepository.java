package phenriqued.github.queue_manager_api.repository.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import phenriqued.github.queue_manager_api.dto.queue.QueueMetricsDTO;
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

    @Query("""
            SELECT new phenriqued.github.queue_manager_api.dto.queue.QueueMetricsDTO(
                CAST(COUNT(t) AS int),
                CAST(COUNT(CASE WHEN t.status = 'PENDING' THEN 1 END) AS int),
                CAST(COUNT(CASE WHEN t.status = 'COMPLETED' THEN 1 END) AS int),
                CAST(COUNT(CASE WHEN t.status = 'CANCELLED' THEN 1 END) AS int),
                CAST(COUNT(CASE WHEN t.status = 'MISSED' THEN 1 END) AS int),
                COALESCE(AVG(CASE WHEN t.status = 'MISSED' THEN CAST(TIMESTAMPDIFF(SECOND, t.createdAt, t.startAt) AS double) END), 0.0),
                COALESCE(AVG(CASE WHEN t.status = 'COMPLETED' THEN CAST(TIMESTAMPDIFF(SECOND, t.startAt, t.finishedAt) AS double) END), 0.0)
            )
            FROM TicketEntity t
            WHERE t.queue.id = :queueId 
            """)
    Optional<QueueMetricsDTO> getAllQueueMetrics(@Param("queueId") Long queueId);



}
