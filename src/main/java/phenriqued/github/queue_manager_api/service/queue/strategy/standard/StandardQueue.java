package phenriqued.github.queue_manager_api.service.queue.strategy.standard;

import org.springframework.stereotype.Component;
import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;
import phenriqued.github.queue_manager_api.service.queue.state.QueueState;
import phenriqued.github.queue_manager_api.service.queue.strategy.TicketCallingStrategy;

import java.util.Optional;

@Component
public class StandardQueue implements TicketCallingStrategy {

    private static final String DEFAULT_QUEUE = "DEFAULT";

    @Override
    public Optional<TicketEntity> callNextTicket(QueueState queueState) {
        return queueState.poll(DEFAULT_QUEUE);
    }
}
