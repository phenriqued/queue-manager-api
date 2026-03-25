package phenriqued.github.queue_manager_api.service.ticket.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TicketCodeGenerator {

    private final AtomicInteger normalCounter = new AtomicInteger(0);
    private final AtomicInteger priorityCounter = new AtomicInteger(0);

    public String generateNextCode(Boolean isPriority){

        int number = isPriority ?
                        priorityCounter.incrementAndGet() :
                        normalCounter.incrementAndGet();

        String prefix = isPriority ? "P" : "N";
        return String.format("%s-%04d", prefix, number);
    }



}
