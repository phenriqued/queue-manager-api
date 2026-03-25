package phenriqued.github.queue_manager_api.service.ticket;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import phenriqued.github.queue_manager_api.dto.ticket.TicketRequestDTO;
import phenriqued.github.queue_manager_api.dto.ticket.TicketResponseDTO;
import phenriqued.github.queue_manager_api.model.customer.CustomerEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;
import phenriqued.github.queue_manager_api.model.ticket.TypeTicket;
import phenriqued.github.queue_manager_api.repository.customer.CustomerRepository;
import phenriqued.github.queue_manager_api.repository.ticket.TicketRepository;
import phenriqued.github.queue_manager_api.service.ticket.utils.TicketCodeGenerator;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final TicketCodeGenerator codeGenerator;

    public TicketService(TicketRepository ticketRepository, CustomerRepository customerRepository, TicketCodeGenerator codeGenerator) {
        this.ticketRepository = ticketRepository;
        this.customerRepository = customerRepository;
        this.codeGenerator = codeGenerator;
    }


    public TicketResponseDTO issueTicket(TicketRequestDTO request) {
        if(request.ownerCPF() == null && request.isManualPriority() == null) throw new NullPointerException();

        String code;
        TypeTicket typeTicket;
        TicketEntity ticket;

        if (request.ownerCPF() != null && !request.ownerCPF().isBlank()){
            CustomerEntity owner = customerRepository.findByCpf(request.ownerCPF())
                    .orElseThrow(() -> new EntityNotFoundException("Customer was not found!"));

            code = codeGenerator.generateNextCode(owner.getIsPriority());
            typeTicket = owner.getIsPriority() ? TypeTicket.PRIORITY : TypeTicket.NORMAL;
            ticket = new TicketEntity(owner, code, typeTicket);

        }else {
            code = codeGenerator.generateNextCode(request.isManualPriority());
            typeTicket = request.isManualPriority() ? TypeTicket.PRIORITY : TypeTicket.NORMAL;
            ticket = new TicketEntity(code, typeTicket);
        }
        ticketRepository.save(ticket);
        return new TicketResponseDTO(ticket);
    }



}
