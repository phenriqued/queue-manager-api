package phenriqued.github.queue_manager_api.service.queue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import phenriqued.github.queue_manager_api.infra.exception.custom.InvalidQueueTransitionException;
import phenriqued.github.queue_manager_api.model.queue.QueueEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;
import phenriqued.github.queue_manager_api.model.ticket.TypeTicket;
import phenriqued.github.queue_manager_api.repository.queue.QueueRepository;
import phenriqued.github.queue_manager_api.repository.ticket.TicketRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class QueueServiceLifecycleTest {

    @Autowired
    private QueueService queueService;
    @Autowired
    private QueueRepository queueRepository;
    @Autowired
    private TicketRepository ticketRepository;

    @Test
    @DisplayName("should add a ticket to an existing queue.")
    void addToQueue() {
        QueueEntity queue = queueRepository.findById(1L).orElseThrow();
        TicketEntity ticket = new TicketEntity("P-10001", TypeTicket.PRIORITY, queue);
        queueService.addToQueue(ticket);

        var responseTicket = queueService.callNext(1L);
        assertEquals(responseTicket.id(), 1L);
        assertEquals(responseTicket.queueName(), queue.getNameQueue());
        assertEquals(responseTicket.code(), "P-10001");
    }
    @Test
    @DisplayName("should not add ticket when this ticket is null.")
    void shouldNotAddTicketWhenTicketIsNull() {
        TicketEntity ticket = null;
        assertThrows(NullPointerException.class, () -> queueService.addToQueue(ticket));
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
        QueueEntity queue = queueRepository.findById(1L).orElseThrow();
        List<TicketEntity> tickets = List.of(new TicketEntity("P-10001", TypeTicket.PRIORITY, queue),
                new TicketEntity("N-10001", TypeTicket.NORMAL, queue),
                new TicketEntity("P-10002", TypeTicket.PRIORITY, queue),
                new TicketEntity("N-10002", TypeTicket.NORMAL, queue));
        ticketRepository.saveAll(tickets);
        var numberOfTicketsInQueue = queueService.getSize(1L);
        assertEquals(4, numberOfTicketsInQueue);
    }
}