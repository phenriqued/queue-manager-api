package phenriqued.github.queue_manager_api.service.queue.state;

import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;

import java.util.Optional;

public interface QueueState {

    void push(String category, TicketEntity ticket);
    Optional<TicketEntity> poll(String category);

    boolean hasTickets(String category);

    int getCounter(String key);
    void incrementCounter(String key);
    void resetCounter(String key);

}
