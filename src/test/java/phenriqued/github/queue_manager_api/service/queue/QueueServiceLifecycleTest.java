package phenriqued.github.queue_manager_api.service.queue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import phenriqued.github.queue_manager_api.infra.exception.custom.InvalidQueueTransitionException;
import phenriqued.github.queue_manager_api.model.queue.QueueEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;
import phenriqued.github.queue_manager_api.model.ticket.TypeTicket;
import phenriqued.github.queue_manager_api.repository.queue.QueueRepository;
import phenriqued.github.queue_manager_api.repository.ticket.TicketRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class QueueServiceLifecycleTest {

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

}