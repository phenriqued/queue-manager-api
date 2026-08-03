package phenriqued.github.queue_manager_api.service.queue.strategy.preferentialPriority;

import org.springframework.stereotype.Component;
import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;
import phenriqued.github.queue_manager_api.service.queue.state.QueueState;
import phenriqued.github.queue_manager_api.service.queue.strategy.TicketCallingStrategy;

import java.util.Optional;

@Component
public class PreferentialPriorityStrategy implements TicketCallingStrategy {

    private static final String PRIORITY_CAT = "PRIORITY";
    private static final String NORMAL_CAT = "NORMAL";
    private static final String COUNTER_KEY = "pref_calls";

    @Override
    public Optional<TicketEntity> callNextTicket(QueueState memoryQueue) {

        int calledCount = memoryQueue.getCounter(COUNTER_KEY);
        Optional<TicketEntity> ticket = Optional.empty();

        if (calledCount >= 2 && memoryQueue.hasTickets(NORMAL_CAT)) {
            memoryQueue.resetCounter(COUNTER_KEY);
            ticket = memoryQueue.poll(NORMAL_CAT);
        } else if (memoryQueue.hasTickets(PRIORITY_CAT)) {
            memoryQueue.incrementCounter(COUNTER_KEY);
            ticket = memoryQueue.poll(PRIORITY_CAT);
        } else if (memoryQueue.hasTickets(NORMAL_CAT)) {
            memoryQueue.resetCounter(COUNTER_KEY);
            ticket = memoryQueue.poll(NORMAL_CAT);
        }
        return ticket;
    }


}
