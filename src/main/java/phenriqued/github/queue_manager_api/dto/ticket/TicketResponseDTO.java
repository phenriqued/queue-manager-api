package phenriqued.github.queue_manager_api.dto.ticket;

import phenriqued.github.queue_manager_api.model.customer.CustomerEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketStatus;
import phenriqued.github.queue_manager_api.model.ticket.TypeTicket;

import java.time.LocalDateTime;

public record TicketResponseDTO(

        Long id,
        CustomerEntity owner,
        String code,
        TypeTicket typeTicket,
        TicketStatus status,
        LocalDateTime createdAt,
        String queueName) {

    public TicketResponseDTO(TicketEntity entity){
        this(entity.getId(), entity.getOwner(), entity.getCode(), entity.getTypeTicket(),
                entity.getStatus(), entity.getCreatedAt(), entity.getQueue().getNameQueue());
    }

}
