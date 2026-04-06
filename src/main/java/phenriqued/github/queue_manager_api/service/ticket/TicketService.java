package phenriqued.github.queue_manager_api.service.ticket;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phenriqued.github.queue_manager_api.dto.ticket.TicketRequestDTO;
import phenriqued.github.queue_manager_api.dto.ticket.TicketResponseDTO;
import phenriqued.github.queue_manager_api.dto.ticket.TicketUpdateRequestDTO;
import phenriqued.github.queue_manager_api.infra.exception.custom.IllegalDataException;
import phenriqued.github.queue_manager_api.model.customer.CustomerEntity;
import phenriqued.github.queue_manager_api.model.queue.QueueEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;
import phenriqued.github.queue_manager_api.model.ticket.TypeTicket;
import phenriqued.github.queue_manager_api.repository.customer.CustomerRepository;
import phenriqued.github.queue_manager_api.repository.ticket.TicketRepository;
import phenriqued.github.queue_manager_api.service.queue.QueueService;
import phenriqued.github.queue_manager_api.service.ticket.utils.TicketCodeGenerator;

import java.util.HashMap;
import java.util.Map;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final QueueService queueService;
    private final Map<Long, TicketCodeGenerator> codeGenerator = new HashMap<>();

    public TicketService(TicketRepository ticketRepository, CustomerRepository customerRepository,
                         QueueService queueService) {
        this.ticketRepository = ticketRepository;
        this.customerRepository = customerRepository;
        this.queueService = queueService;
    }


    public TicketResponseDTO issueTicket(TicketRequestDTO request) {
        if(request.ownerCPF() == null && request.isManualPriority() == null) throw new IllegalDataException("It is not possible to issue without information!");
        var queue = queueService.findQueueById(request.queue());
        codeGenerator.putIfAbsent(queue.getId(), new TicketCodeGenerator(queue.getId()));
        String code;
        TypeTicket typeTicket;
        TicketEntity ticket;

        if (request.ownerCPF() != null && !request.ownerCPF().isBlank()){
            CustomerEntity owner = customerRepository.findByCpf(request.ownerCPF())
                    .orElseThrow(() -> new EntityNotFoundException("Customer was not found!"));
            code = codeGenerator.get(queue.getId()).generateNextCode(owner.getIsPriority());
            typeTicket = owner.getIsPriority() ? TypeTicket.PRIORITY : TypeTicket.NORMAL;
            ticket = new TicketEntity(owner, code, typeTicket, queue);

        }else {
            code = codeGenerator.get(queue.getId()).generateNextCode(request.isManualPriority());
            typeTicket = request.isManualPriority() ? TypeTicket.PRIORITY : TypeTicket.NORMAL;
            ticket = new TicketEntity(code, typeTicket, queue);
        }
        ticketRepository.save(ticket);
        queueService.addToQueue(ticket);
        return new TicketResponseDTO(ticket);
    }

    public TicketResponseDTO findById(Long id) {
        return ticketRepository.findByIdWithCustomer(id)
                .map(TicketResponseDTO::new)
                .orElseThrow(() -> new EntityNotFoundException("Ticket was not found!"));
    }
    public TicketEntity findTicketEntityById(Long id) {
        return ticketRepository.findByIdWithCustomer(id)
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
            ticket.changeOwner(customerRepository.findByCpf(updateDTO.ownerCPF())
                    .orElseThrow(() -> new EntityNotFoundException("Customer was not found!")));
        }
        if (updateDTO.queue() != null){
            ticket.setQueue(queueService.findQueueById(updateDTO.queue()));
            queueService.addToQueue(ticket);
        }
    }

    public void deleteById(Long id) {
        if (ticketRepository.existsById(id)){
            ticketRepository.deleteById(id);
            return;
        }
        throw new EntityNotFoundException("Ticket was not found!");
    }

    @Transactional
    public void completeTicket(Long id) {
        TicketEntity ticket = findTicketEntityById(id);
        ticket.statusCompleted();
    }
    @Transactional
    public void missTicket(Long id) {
        TicketEntity ticket = findTicketEntityById(id);
        ticket.statusMissed();
    }
    @Transactional
    public void cencelTicket(Long id) {
        TicketEntity ticket = findTicketEntityById(id);
        queueService.removeTicketFromQueue(ticket);
        ticket.statusCancel();
    }


}
