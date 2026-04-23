package phenriqued.github.queue_manager_api.service.ticket;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import phenriqued.github.queue_manager_api.dto.customer.CreateCustomerDTO;
import phenriqued.github.queue_manager_api.dto.ticket.TicketRequestDTO;
import phenriqued.github.queue_manager_api.dto.ticket.TicketUpdateRequestDTO;
import phenriqued.github.queue_manager_api.infra.exception.custom.IllegalDataException;
import phenriqued.github.queue_manager_api.infra.exception.custom.InvalidTicketOperationException;
import phenriqued.github.queue_manager_api.model.customer.CustomerEntity;
import phenriqued.github.queue_manager_api.model.queue.QueueEntity;
import phenriqued.github.queue_manager_api.repository.customer.CustomerRepository;
import phenriqued.github.queue_manager_api.repository.queue.QueueRepository;
import phenriqued.github.queue_manager_api.service.queue.QueueService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TicketServiceUpdatesAndDeleteTest {

    @Autowired
    private TicketService ticketService;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private QueueRepository queueRepository;
    @Autowired
    private QueueService queueService;

    private CustomerEntity sharedCustomer;

    @BeforeAll
    @Transactional
    void setupAll() {
        CustomerEntity customer = new CustomerEntity(new CreateCustomerDTO(
                "Teste", "37729817128", "11999999999", LocalDate.of(2002, 5, 23)));
        this.sharedCustomer = customerRepository.save(customer);
    }

    @Rollback
    @Transactional
    @BeforeEach
    void setup(){
        QueueEntity queue = queueRepository.findById(1L).orElseThrow();
        TicketRequestDTO ticket1 = new TicketRequestDTO(null, true, queue.getId());
        TicketRequestDTO ticket2 = new TicketRequestDTO(null, false, queue.getId());
        TicketRequestDTO ticket3 = new TicketRequestDTO(null, true, queue.getId());
        TicketRequestDTO ticket4 = new TicketRequestDTO(null, true, queue.getId());
        TicketRequestDTO ticketDelete = new TicketRequestDTO(null, true, queue.getId());
        ticketService.issueTicket(ticket1);
        ticketService.issueTicket(ticket2);
        ticketService.issueTicket(ticket3);
        ticketService.issueTicket(ticket4);
        ticketService.issueTicket(ticketDelete);
        customerRepository.save(sharedCustomer);
    }

    @Test
    @DisplayName("should update a ticket by linking it to the existing customer's CPF")
    void shouldUpdateTicketByLinkingToExistingCustomer() {
        TicketUpdateRequestDTO updateRequest = new TicketUpdateRequestDTO("37729817128", null);

        ticketService.updateTicket(2L, updateRequest);

        var ticketUpdated = ticketService.findById(2L);
        assertEquals("37729817128", ticketUpdated.owner().getCpf());
        assertEquals("Teste", ticketUpdated.owner().getName());
    }
    @Test
    @DisplayName("should update the ticket by linking it to a new queue.")
    void shouldUpdateTicketByLinkingToNewQueue() {

        TicketUpdateRequestDTO updateRequest = new TicketUpdateRequestDTO(null, 2L);
        queueService.callNext(1L);

        ticketService.updateTicket(1L, updateRequest);

        var ticketUpdated = ticketService.findTicketEntityById(1L);
        ticketUpdated.startAttendance();
        assertEquals(2L, ticketUpdated.getQueue().getId());
        assertEquals("Caixa", ticketUpdated.getQueue().getNameQueue());
        assertNull(ticketUpdated.getOwner());
    }
    @Test
    @DisplayName("It should update both the queue and link the ticket to a customer.")
    void shouldUpdateTicketByLinkingToNewQueueAndCustomer() {
        TicketUpdateRequestDTO updateRequest = new TicketUpdateRequestDTO("37729817128", 2L);
        queueService.callNext(1L);

        ticketService.updateTicket(3L, updateRequest);

        var ticketUpdated = ticketService.findTicketEntityById(3L);
        ticketUpdated.startAttendance();
        assertEquals(2L, ticketUpdated.getQueue().getId());
        assertEquals("Caixa", ticketUpdated.getQueue().getNameQueue());
        assertEquals("37729817128", ticketUpdated.getOwner().getCpf());
        assertEquals("Teste", ticketUpdated.getOwner().getName());
    }
    @Test
    @DisplayName("It should throw an exception when the CPF and Id in the queue are null.")
    void shouldThrowExceptionWhenCPFAndIdQueueIsNull() {
        TicketUpdateRequestDTO updateRequest = new TicketUpdateRequestDTO(null, null);

        Exception exception = assertThrows(IllegalDataException.class, () -> ticketService.updateTicket(1L, updateRequest));
        assertEquals("Owner's CPF and queue cannot be null.", exception.getMessage());
    }
    @Test
    @DisplayName("It should throw an exception when trying to change to the same queue.")
    void shouldThrowExceptionWhenTryingToChangeSameQueue() {
        TicketUpdateRequestDTO updateRequest = new TicketUpdateRequestDTO(null, 1L);

        Exception exception = assertThrows(IllegalDataException.class, () -> ticketService.updateTicket(4L, updateRequest));
        assertEquals("It is not possible to update the ticket queue to the queue itself.", exception.getMessage());
    }
    @Test
    @DisplayName("It should throw an exception when updating to a non-existent client.")
    void shouldThrowExceptionWhenUpdatingToNonExistentClient() {
        TicketUpdateRequestDTO updateRequest = new TicketUpdateRequestDTO("12345678911", null);

        Exception exception = assertThrows(EntityNotFoundException.class, () -> ticketService.updateTicket(4L, updateRequest));
        assertEquals("Customer was not found!", exception.getMessage());
    }
    @Test
    @DisplayName("It should throw an exception when updating to a client, but the ticket status is different from pending or in progress.")
    @Transactional
    void shouldThrowExceptionWhenUpdatingToExistentClientButTicketStatusIsDifferentFromPendingOrInProgress() {
        TicketUpdateRequestDTO updateRequest = new TicketUpdateRequestDTO("37729817128", null);
        var ticketStatusComplete = ticketService.findTicketEntityById(4L);
        ticketStatusComplete.startAttendance();
        ticketStatusComplete.statusCompleted();
        Exception exception = assertThrows(InvalidTicketOperationException.class, () -> ticketService.updateTicket(4L, updateRequest));
        assertEquals("It is not possible to change the owner when the ticket's status is anything other than 'IN_PROGRESS' or 'PENDING'.",
                exception.getMessage());
    }
    @Test
    @DisplayName("It should throw an exception when updating to a non-existent ticket.")
    void shouldThrowExceptionWhenUpdatingToNonExistentTicket() {
        TicketUpdateRequestDTO updateRequest = new TicketUpdateRequestDTO("12345678911", null);

        Exception exception = assertThrows(EntityNotFoundException.class, () -> ticketService.updateTicket(999L, updateRequest));
        assertEquals("Ticket was not found!", exception.getMessage());
    }

    @Test
    @DisplayName("should delete an existing ticket")
    void deleteExistingTicket() {
        ticketService.deleteById(5L);
        assertThrows(EntityNotFoundException.class, () -> ticketService.findTicketEntityById(5L));
    }
    @Test
    @DisplayName("should not delete ticket that does not exist")
    void shouldNotDeleteTicketNotExist() {
        Exception exception = assertThrows(EntityNotFoundException.class, () -> ticketService.deleteById(9999L));
        assertEquals("Ticket was not found!", exception.getMessage());
    }

}