package phenriqued.github.queue_manager_api.service.queue.strategy;

import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;
import phenriqued.github.queue_manager_api.service.queue.state.QueueState;

import java.util.Optional;

public interface TicketCallingStrategy {

    Optional<TicketEntity> callNextTicket(QueueState queueState);

}
