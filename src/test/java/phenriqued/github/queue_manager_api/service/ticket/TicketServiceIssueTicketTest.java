package phenriqued.github.queue_manager_api.service.ticket;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import phenriqued.github.queue_manager_api.dto.customer.CreateCustomerDTO;
import phenriqued.github.queue_manager_api.dto.ticket.TicketRequestDTO;
import phenriqued.github.queue_manager_api.infra.exception.custom.IllegalDataException;
import phenriqued.github.queue_manager_api.infra.exception.custom.InvalidQueueTransitionException;
import phenriqued.github.queue_manager_api.model.customer.CustomerEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketStatus;
import phenriqued.github.queue_manager_api.model.ticket.TypeTicket;
import phenriqued.github.queue_manager_api.repository.customer.CustomerRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TicketServiceIssueTicketTest {

    @Container
    @ServiceConnection
    static MySQLContainer mySQL = new MySQLContainer("mysql:latest");

    @Autowired
    private TicketService ticketService;
    @Autowired
    private CustomerRepository customerRepository;

    @Rollback
    @Transactional
    @Test
    @DisplayName("It should issue a ticket when the customer's CPF and queue ID are entered, without depending on whether it's a priority.")
    void shouldIssueTicketWhenCustomerCPFAndQueueIdAreEntered() {
        customerRepository.save(new CustomerEntity(new CreateCustomerDTO("Teste", "37729817128", "11999999999",
                LocalDate.of(2002, 5, 23))));
        TicketRequestDTO ticketRequest = new TicketRequestDTO("37729817128", null, 1L);

        var ticketIssued = ticketService.issueTicket(ticketRequest);
        assertEquals("37729817128", ticketIssued.owner().getCpf());
        assertEquals("N-", ticketIssued.code().substring(0,2));
        assertEquals(7, ticketIssued.code().length());
        assertEquals(TypeTicket.NORMAL, ticketIssued.typeTicket());
        assertEquals(TicketStatus.PENDING, ticketIssued.status());
    }
    @Test
    @DisplayName("It should issue a ticket when priority is manually entered and the customer's queue ID is entered, regardless of whether or not the CPF is provided.")
    void shouldIssueTicketWhenIsManualPriorityAndQueueIdAreEntered() {
        TicketRequestDTO ticketRequest = new TicketRequestDTO(null, false, 1L);

        var ticketIssued = ticketService.issueTicket(ticketRequest);
        assertNull(ticketIssued.owner());
        assertEquals("N-", ticketIssued.code().substring(0,2));
        assertEquals(7, ticketIssued.code().length());
        assertEquals(TypeTicket.NORMAL, ticketIssued.typeTicket());
        assertEquals(TicketStatus.PENDING, ticketIssued.status());
    }
    @Rollback
    @Transactional
    @Test
    @DisplayName("When the customer's CPF is entered and the priority is manually determined, the ticket should be issued based on the CPF.")
    void shouldIssueTicketBasedOnCPFWhenBothPiecesOfInformationAreProvided() {
        //Customer is not a priority
        customerRepository.save(new CustomerEntity(new CreateCustomerDTO("Teste", "37729817128", "11999999999",
                LocalDate.of(2002, 5, 23))));
        //Customer is not a priority, but manually assigning that priority will make them so
        TicketRequestDTO ticketRequest = new TicketRequestDTO("37729817128", true, 1L);

        var ticketIssued = ticketService.issueTicket(ticketRequest);
        assertEquals("37729817128", ticketIssued.owner().getCpf());
        assertEquals("N-", ticketIssued.code().substring(0,2));
        assertEquals(7, ticketIssued.code().length());
        assertEquals(TypeTicket.NORMAL, ticketIssued.typeTicket());
        assertEquals(TicketStatus.PENDING, ticketIssued.status());
    }

    @Test
    @DisplayName("A ticket should not be issued when the queue ID is not provided.")
    void shouldNotIssueTicketWhenQueueIDIsNotProvided() {
        TicketRequestDTO ticketRequest = new TicketRequestDTO("37729817128", false, null);

        Exception exception = assertThrows(InvalidQueueTransitionException.class, () -> ticketService.issueTicket(ticketRequest));
        assertEquals("It is not possible to search for a queue whose ID is equal to null.", exception.getMessage());
    }
    @Test
    @DisplayName("A ticket should not be issued when the CPF or priority information is not provided.")
    void shouldNotBeIssuedWhenCPFAndIsManualPriorityIsNotProvided() {
        TicketRequestDTO ticketRequest = new TicketRequestDTO(null, null, 1L);

        Exception exception = assertThrows(IllegalDataException.class, () -> ticketService.issueTicket(ticketRequest));
        assertEquals("It is not possible to issue without information!", exception.getMessage());
    }
    @Test
    @DisplayName("A ticket should not be issued when the CPF is provided the incorrect form.")
    void shouldNotBeIssuedWhenCPFIsProvidedButIsIncorrect() {
        TicketRequestDTO ticketRequest = new TicketRequestDTO("12345678911", null, 1L);

        Exception exception = assertThrows(EntityNotFoundException.class, () -> ticketService.issueTicket(ticketRequest));
        assertEquals("Customer was not found!", exception.getMessage());
    }

}