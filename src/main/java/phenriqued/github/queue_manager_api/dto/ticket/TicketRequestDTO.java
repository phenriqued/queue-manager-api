package phenriqued.github.queue_manager_api.dto.ticket;

import phenriqued.github.queue_manager_api.infra.validation.cpf.ValidCPF;

public record TicketRequestDTO(
        @ValidCPF
        String ownerCPF,
        Boolean isManualPriority) {

}
