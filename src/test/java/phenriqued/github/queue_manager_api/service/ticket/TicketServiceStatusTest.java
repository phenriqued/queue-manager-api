package phenriqued.github.queue_manager_api.service.ticket;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import phenriqued.github.queue_manager_api.dto.ticket.TicketRequestDTO;
import phenriqued.github.queue_manager_api.infra.exception.custom.InvalidTicketOperationException;
import phenriqued.github.queue_manager_api.model.queue.QueueEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketStatus;
import phenriqued.github.queue_manager_api.repository.queue.QueueRepository;
import phenriqued.github.queue_manager_api.repository.ticket.TicketRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TicketServiceStatusTest {

    @Autowired
    private TicketService ticketService;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private QueueRepository queueRepository;

    @Rollback
    @Transactional
    @BeforeAll
    void setup(){
        QueueEntity queue = queueRepository.findById(1L).orElseThrow();
        TicketRequestDTO ticket1 = new TicketRequestDTO(null, true, queue.getId());
        TicketRequestDTO ticket2 = new TicketRequestDTO(null, true, queue.getId());
        TicketRequestDTO ticket3 = new TicketRequestDTO(null, true, queue.getId());
        ticketService.issueTicket(ticket1);
        ticketService.issueTicket(ticket2);
        ticketService.issueTicket(ticket3);
    }

    @Test
    @DisplayName("The ticket status should be marked as COMPLETED when the status is already IN_PROGRESS.")
    @Transactional
    void completeTicket() {
        TicketEntity ticket = ticketService.findTicketEntityById(1L);
        ticket.startAttendance();
        ticketService.completeTicket(1L);
        assertEquals(TicketStatus.COMPLETED, ticket.getStatus());
    }
    @Test
    @DisplayName("It should throw an exception when it wants to mark the status as COMPLETE and the ticket status is different from IN_PROGRESS.")
    void shouldThrowExceptionWhenTicketStatusIsOtherThanInProgress() {
        Exception exception = assertThrows(InvalidTicketOperationException.class, () -> ticketService.completeTicket(1L));
        assertEquals("It is not possible to complete a ticket that is not in progress.", exception.getMessage());
    }

    @Test
    @DisplayName("The ticket status should be marked as MISS when the status is IN_PROGRESS.")
    @Transactional
    void missTicket() {
        TicketEntity ticket = ticketService.findTicketEntityById(2L);
        ticket.startAttendance();
        ticketService.missTicket(2L);
        assertEquals(TicketStatus.MISSED, ticket.getStatus());
    }
    @Test
    @DisplayName("It should throw an exception when it wants to mark the status as COMPLETE and the ticket status is different from IN_PROGRESS.")
    void shouldThrowExceptionWhenTicketStatusIsOtherThanInProgressForMarkMissed() {
        Exception exception = assertThrows(InvalidTicketOperationException.class, () -> ticketService.missTicket(2L));
        assertEquals("It is not possible to mark a ticket as miss once it has been completed.", exception.getMessage());
    }

    @Test
    @DisplayName("The ticket status should be marked as CANCELLED when the status is PENDING.")
    @Transactional
    void cancelTicket() {
        TicketEntity ticket = ticketService.findTicketEntityById(3L);
        ticketService.cancelTicket(3L);
        assertEquals(TicketStatus.CANCELLED, ticket.getStatus());
    }
    @Test
    @DisplayName("It should throw an exception when it wants to mark the status as CANCELLED and the ticket status is different from PENDING.")
    @Transactional
    void shouldThrowExceptionWhenTicketStatusIsOtherThanPendingForMarkCancel() {
        TicketEntity ticket = ticketService.findTicketEntityById(3L);
        ticket.startAttendance();
        Exception exception = assertThrows(InvalidTicketOperationException.class, () -> ticketService.cancelTicket(3L));
        assertEquals("It is not possible to cancel a ticket that is completed or miss.", exception.getMessage());
    }
}