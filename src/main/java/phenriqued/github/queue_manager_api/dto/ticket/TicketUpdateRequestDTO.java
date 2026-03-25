package phenriqued.github.queue_manager_api.dto.ticket;

import phenriqued.github.queue_manager_api.model.customer.CustomerEntity;

public record TicketUpdateRequestDTO(
        String ownerCPF) {

    public TicketUpdateRequestDTO{
        if(ownerCPF != null && !ownerCPF.isBlank()){
            if (!CustomerEntity.isValidCPF(ownerCPF)) throw new IllegalArgumentException("Invalid CPF");
        }
    }
}
