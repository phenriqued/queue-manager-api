package phenriqued.github.queue_manager_api.service.queue;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import phenriqued.github.queue_manager_api.model.queue.QueueEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketStatus;
import phenriqued.github.queue_manager_api.model.ticket.TypeTicket;
import phenriqued.github.queue_manager_api.repository.queue.QueueRepository;
import phenriqued.github.queue_manager_api.repository.ticket.TicketRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class QueueServiceMetricsTest {

    @Container
    @ServiceConnection
    static MySQLContainer mySQL = new MySQLContainer("mysql:latest");

    @Autowired
    private QueueService queueService;
    @Autowired
    private QueueRepository queueRepository;
    @Autowired
    private TicketRepository ticketRepository;

    private QueueEntity queue;
    private List<TicketEntity> ticketSaved = new ArrayList<>();

    @BeforeAll
    void setupAll(){
        this.queue = queueRepository.findById(1L).orElseThrow();
        List<TicketEntity> tickets = List.of(
                new TicketEntity("P-10001", TypeTicket.PRIORITY, queue),
                new TicketEntity("N-10001", TypeTicket.NORMAL, queue),
                new TicketEntity("N-10002", TypeTicket.NORMAL, queue),
                new TicketEntity("N-10003", TypeTicket.NORMAL, queue),
                new TicketEntity("P-10002", TypeTicket.PRIORITY, queue),
                new TicketEntity("N-10004", TypeTicket.NORMAL, queue));
        ticketSaved.addAll(ticketRepository.saveAll(tickets));
    }

    @Test
    @DisplayName("should return queue metrics, such as total tickets, tickets served, canceled, lost, average wait time and service time.")
    void shouldReturnQueueMetrics(){
        var ticketMissedBefore = ticketRepository.findById(ticketSaved.get(1).getId()).orElseThrow();
        LocalDateTime createdAtReal = ticketMissedBefore.getCreatedAt();

        updateTickets(ticketSaved.get(0).getId(), ticketSaved.get(1).getId(), ticketSaved.get(2).getId(), createdAtReal);

        var queueMetrics = queueService.queueMetricsByID(queue.getId());

        assertEquals(6, queueMetrics.totalTickets());
        assertEquals(3, queueMetrics.totalPending());
        assertEquals(1, queueMetrics.totalTicketsCompleted());
        assertEquals(1, queueMetrics.totalTicketsMissed());
        assertEquals(1, queueMetrics.totalTicketsCancel());

        long tempoEsperaEsperado = java.time.Duration.between(createdAtReal, createdAtReal.plusMinutes(20)).toSeconds();

        assertEquals((double) tempoEsperaEsperado, queueMetrics.averageWaitingTime(), 0.001);
        assertEquals(600.0, queueMetrics.averageServiceTime(), 0.001);
    }

    private void updateTickets(Long ticketIdOne, Long ticketIdTwo, Long ticketIdThree, LocalDateTime baseTime){
        var ticketCompleted = ticketRepository.findById(ticketIdOne).orElseThrow();
        ReflectionTestUtils.setField(ticketCompleted, "startAt", baseTime.plusMinutes(20));
        ReflectionTestUtils.setField(ticketCompleted, "finishedAt", baseTime.plusMinutes(30));
        ReflectionTestUtils.setField(ticketCompleted, "status", TicketStatus.COMPLETED);
        ticketRepository.save(ticketCompleted);

        var ticketMissed = ticketRepository.findById(ticketIdTwo).orElseThrow();
        ReflectionTestUtils.setField(ticketMissed, "startAt", baseTime.plusMinutes(20));
        ReflectionTestUtils.setField(ticketMissed, "finishedAt", baseTime.plusMinutes(25));
        ReflectionTestUtils.setField(ticketMissed, "status", TicketStatus.MISSED);
        ticketRepository.save(ticketMissed);

        var ticketCancel = ticketRepository.findById(ticketIdThree).orElseThrow();
        ticketCancel.statusCancel();
        ticketRepository.save(ticketCancel);
    }
    @Test
    @DisplayName("It should return the metrics to zero when there are no tickets in the queue.")
    @Transactional
    void shouldReturnMetricsToZeroWhereNoTicketsInQueue(){
        QueueEntity queue = queueRepository.findById(2L).orElseThrow();

        var queueMetrics = queueService.queueMetricsByID(queue.getId());

        assertEquals(0, queueMetrics.totalTickets());
        assertEquals(0, queueMetrics.totalPending());
        assertEquals(0, queueMetrics.totalTicketsCompleted());
        assertEquals(0, queueMetrics.totalTicketsMissed());
        assertEquals(0, queueMetrics.totalTicketsCancel());
        assertEquals(0.0, queueMetrics.averageWaitingTime());
        assertEquals(0.0, queueMetrics.averageServiceTime());
    }
    @Test
    @DisplayName("It should return the queue metric, when there are only pending tickets, returning only those.")
    @Transactional
    void shouldReturnQueueMetricsWhenAreOnlyPendingTickets(){
        QueueEntity queue = queueRepository.findById(3L).orElseThrow();
        List<TicketEntity> tickets = List.of(new TicketEntity("P-10001", TypeTicket.PRIORITY, queue),
                new TicketEntity("N-10001", TypeTicket.NORMAL, queue),
                new TicketEntity("N-10002", TypeTicket.NORMAL, queue),
                new TicketEntity("N-10003", TypeTicket.NORMAL, queue),
                new TicketEntity("P-10002", TypeTicket.PRIORITY, queue),
                new TicketEntity("N-10004", TypeTicket.NORMAL, queue));
        ticketRepository.saveAll(tickets);

        var queueMetrics = queueService.queueMetricsByID(queue.getId());

        assertEquals(6, queueMetrics.totalTickets());
        assertEquals(6, queueMetrics.totalPending());
        assertEquals(0, queueMetrics.totalTicketsCompleted());
        assertEquals(0, queueMetrics.totalTicketsMissed());
        assertEquals(0, queueMetrics.totalTicketsCancel());
        assertEquals(0.0, queueMetrics.averageWaitingTime());
        assertEquals(0.0, queueMetrics.averageServiceTime());
    }
    @Test
    @DisplayName("It should throw an exception when inserting a non-existent queue.")
    @Transactional
    void shouldThrowExceptionWhenInsertingNonExistentQueueID(){
        Exception exception = assertThrows(EntityNotFoundException.class, () -> queueService.queueMetricsByID(999L));
        assertEquals("Queue was not found!", exception.getMessage());
    }

}