package phenriqued.github.queue_manager_api.service.ticket;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phenriqued.github.queue_manager_api.dto.ticket.TicketRequestDTO;
import phenriqued.github.queue_manager_api.dto.ticket.TicketResponseDTO;
import phenriqued.github.queue_manager_api.dto.ticket.TicketUpdateRequestDTO;
import phenriqued.github.queue_manager_api.infra.exception.IllegalDataException;
import phenriqued.github.queue_manager_api.model.customer.CustomerEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;
import phenriqued.github.queue_manager_api.model.ticket.TypeTicket;
import phenriqued.github.queue_manager_api.repository.ticket.TicketRepository;
import phenriqued.github.queue_manager_api.service.customer.CustomerService;
import phenriqued.github.queue_manager_api.service.queue.QueueService;
import phenriqued.github.queue_manager_api.service.ticket.utils.TicketCodeGenerator;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final QueueService queueService;
    private final CustomerService customerService;
    private final TicketCodeGenerator codeGenerator;

    public TicketService(TicketRepository ticketRepository, QueueService queueService,
                         CustomerService customerService, TicketCodeGenerator codeGenerator) {
        this.ticketRepository = ticketRepository;
        this.queueService = queueService;
        this.customerService = customerService;
        this.codeGenerator = codeGenerator;
    }


    public TicketResponseDTO issueTicket(TicketRequestDTO request) {
        if(request.ownerCPF() == null && request.isManualPriority() == null) throw new IllegalDataException("It is not possible to issue without information!");
        var queue = queueService.findQueueById(request.queue());
        String code;
        TypeTicket typeTicket;
        TicketEntity ticket;

        if (request.ownerCPF() != null && !request.ownerCPF().isBlank()){
            CustomerEntity owner = customerService.findCustomerByCpf(request.ownerCPF());

            code = codeGenerator.generateNextCode(owner.getIsPriority());
            typeTicket = owner.getIsPriority() ? TypeTicket.PRIORITY : TypeTicket.NORMAL;
            ticket = new TicketEntity(owner, code, typeTicket);

        }else {
            code = codeGenerator.generateNextCode(request.isManualPriority());
            typeTicket = request.isManualPriority() ? TypeTicket.PRIORITY : TypeTicket.NORMAL;
            ticket = new TicketEntity(code, typeTicket);
        }
        ticket.setQueue(queue);
        ticketRepository.save(ticket);
        queueService.addToQueue(ticket);
        return new TicketResponseDTO(ticket);
    }

    public TicketResponseDTO findById(Long id) {
        return ticketRepository.findByIdWithCustomer(id)
                .map(TicketResponseDTO::new)
                .orElseThrow(() -> new EntityNotFoundException("Ticket was not found!"));
    }

    public Page<TicketResponseDTO> findAllTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable).map(TicketResponseDTO::new);
    }

    @Transactional
    public void updateTicket(Long id, TicketUpdateRequestDTO updateDTO){
        if (updateDTO.ownerCPF() == null && updateDTO.queue() == null) return;

        TicketEntity ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket was not found!"));

        if (updateDTO.ownerCPF() != null){
            CustomerEntity owner = customerService.findCustomerByCpf(updateDTO.ownerCPF());
            ticket.setOwner(owner);
            ticket.setTypeTicket(owner.getIsPriority() ? TypeTicket.PRIORITY : TypeTicket.NORMAL);
        }
        if (updateDTO.queue() != null){
            var newQueue = queueService.findQueueById(updateDTO.queue());
            ticket.setQueue(newQueue);
        }
    }

    public void deleteById(Long id) {
        if (ticketRepository.existsById(id)){
            ticketRepository.deleteById(id);
            return;
        }
        throw new EntityNotFoundException("Ticket was not found!");
    }
}
