package phenriqued.github.queue_manager_api.dto.ticket;

import phenriqued.github.queue_manager_api.model.customer.CustomerEntity;

public record TicketRequestDTO(
        String ownerCPF,
        Boolean isManualPriority) {

    public TicketRequestDTO{
        if(ownerCPF != null && !ownerCPF.isBlank()){
            if (!CustomerEntity.isValidCPF(ownerCPF)) throw new IllegalArgumentException("Invalid CPF");
        }
    }

}
