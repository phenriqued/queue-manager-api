package phenriqued.github.queue_manager_api.service.ticket.utils;

import java.util.concurrent.atomic.AtomicInteger;


public class TicketCodeGenerator {

    private Long queueId;
    private final AtomicInteger normalCounter = new AtomicInteger(0);
    private final AtomicInteger priorityCounter = new AtomicInteger(0);

    public TicketCodeGenerator(Long queueId) {
        this.queueId = queueId;
    }

    public String generateNextCode(Boolean isPriority){
        int number = isPriority ?
                        priorityCounter.incrementAndGet() :
                        normalCounter.incrementAndGet();

        String prefix = isPriority ? "P" : "N";
        return String.format("%s-%d%04d", prefix, this.queueId, number);
    }



}
