package phenriqued.github.queue_manager_api.service.queue;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import phenriqued.github.queue_manager_api.infra.exception.custom.InvalidQueueTransitionException;
import phenriqued.github.queue_manager_api.model.queue.QueueEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketStatus;
import phenriqued.github.queue_manager_api.model.ticket.TypeTicket;
import phenriqued.github.queue_manager_api.repository.queue.QueueRepository;
import phenriqued.github.queue_manager_api.repository.ticket.TicketRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QueueServiceLifecycleTest {

    @Autowired
    private QueueService queueService;
    @Autowired
    private QueueRepository queueRepository;
    @Autowired
    private TicketRepository ticketRepository;

    private QueueEntity queue;
    private List<TicketEntity> ticketSaved = new ArrayList<>();

    @BeforeAll
    @Transactional
    void setupAll(){
        this.queue = queueRepository.findById(1L).orElseThrow();
        List<TicketEntity> tickets = List.of(new TicketEntity("P-10001", TypeTicket.PRIORITY, queue),
                new TicketEntity("N-10001", TypeTicket.NORMAL, queue),
                new TicketEntity("N-10002", TypeTicket.NORMAL, queue),
                new TicketEntity("N-10003", TypeTicket.NORMAL, queue),
                new TicketEntity("P-10002", TypeTicket.PRIORITY, queue),
                new TicketEntity("N-10004", TypeTicket.NORMAL, queue));
        ticketSaved.addAll(ticketRepository.saveAll(tickets));
    }

    @Test
    @DisplayName("should add a ticket to an existing queue.")
    void addToQueue() {
        TicketEntity ticket = ticketSaved.getFirst();
        queueService.addToQueue(ticket);

        var responseTicket = queueService.callNext(1L);
        assertEquals(responseTicket.id(), ticket.getId());
        assertEquals(responseTicket.queueName(), queue.getNameQueue());
        assertEquals(responseTicket.code(), ticket.getCode());
    }
    @Test
    @DisplayName("should not add ticket when this ticket is null.")
    void shouldNotAddTicketWhenTicketIsNull() {
        TicketEntity nullTicket = null;
        assertThrows(NullPointerException.class, () -> queueService.addToQueue(nullTicket));
    }
    @Test
    @DisplayName("It should throw an exception when trying to add a ticket with a null queue.")
    void shouldThrowExceptionWhenAddTicketWithNullQueue() {
        TicketEntity ticket = new TicketEntity("P-10001", TypeTicket.PRIORITY, null);
        Exception exception = assertThrows(InvalidQueueTransitionException.class, () -> queueService.addToQueue(ticket));
        assertEquals("It's not possible to add the ticket to the queue when the queue is null. Check the ticket's dependency." ,
                exception.getMessage());
    }

    @Test
    @DisplayName("It should return the number of pending tickets in a queue.")
    void getSize() {
        var numberOfTicketsInQueue = queueService.getSize(queue.getId());
        assertEquals(6, numberOfTicketsInQueue);
    }

    @Test
    @DisplayName("should return queue metrics, such as total tickets, tickets served, canceled, lost, average wait time and service time.")
    @Transactional
    void shouldReturnQueueMetrics(){
        updateTickets(ticketSaved.get(0).getId(), ticketSaved.get(1).getId(), ticketSaved.get(2).getId());

        var queueMetrics = queueService.queueMetricsByID(queue.getId());

        assertEquals(6, queueMetrics.totalTickets());
        assertEquals(3, queueMetrics.totalPending());
        assertEquals(1, queueMetrics.totalTicketsCompleted());
        assertEquals(1, queueMetrics.totalTicketsMissed());
        assertEquals(1, queueMetrics.totalTicketsCancel());
        assertEquals(900.0, queueMetrics.averageWaitingTime(), 0.001);
        assertEquals(900.0, queueMetrics.averageServiceTime(), 0.001);
    }

    private void updateTickets(Long ticketIdOne, Long ticketIdTwo, Long ticketIdThree){
        LocalDateTime now = LocalDateTime.now();

        var ticketCompleted = ticketRepository.findById(ticketIdOne).orElseThrow();
        ReflectionTestUtils.setField(ticketCompleted, "createdAt", now.minusMinutes(30));
        ReflectionTestUtils.setField(ticketCompleted, "startAt", now.minusMinutes(20));
        ReflectionTestUtils.setField(ticketCompleted, "finishedAt", now);
        ReflectionTestUtils.setField(ticketCompleted, "status", TicketStatus.COMPLETED);
        ticketRepository.save(ticketCompleted);

        var ticketMissed = ticketRepository.findById(ticketIdTwo).orElseThrow();
        ReflectionTestUtils.setField(ticketMissed, "createdAt", now.minusMinutes(30));
        ReflectionTestUtils.setField(ticketMissed, "startAt", now.minusMinutes(10));
        ReflectionTestUtils.setField(ticketMissed, "finishedAt", now);
        ReflectionTestUtils.setField(ticketMissed, "status", TicketStatus.MISSED);
        ticketRepository.save(ticketMissed);

        var ticketCancel = ticketRepository.findById(ticketIdThree).orElseThrow();
        ticketCancel.statusCancel();
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