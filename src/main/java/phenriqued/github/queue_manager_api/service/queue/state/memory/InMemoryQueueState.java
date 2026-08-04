package phenriqued.github.queue_manager_api.service.queue.state.memory;

import lombok.Getter;
import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;
import phenriqued.github.queue_manager_api.service.queue.state.QueueState;

import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

@Getter
public class InMemoryQueueState implements QueueState {

    private Long queueId;
    private final Map<String, Queue<TicketEntity>> queues = new ConcurrentHashMap<>();
    private final Map<String, Integer> counters = new ConcurrentHashMap<>();


    private PriorityQueue<TicketEntity> preferentialQueue = new PriorityQueue<>();
    private PriorityQueue<TicketEntity> normalQueue = new PriorityQueue<>();
    private int preferentialCalledCount = 0;
    private final ReentrantLock lock = new ReentrantLock(true);

    public InMemoryQueueState(Long queueId) {
        this.queueId = queueId;
    }

    @Override
    public void push(String category, TicketEntity ticket){
        queues.computeIfAbsent(category, queue -> new ConcurrentLinkedQueue<>()).add(ticket);
    }

    @Override
    public Optional<TicketEntity> poll(String category) {
        Queue<TicketEntity> queue = queues.get(category);
        if (queue == null) return Optional.empty();
        return Optional.ofNullable(queue.poll());
    }

    @Override
    public boolean hasTickets(String category) {
        Queue<TicketEntity> queue = queues.get(category);
        return queue != null && !queue.isEmpty();
    }

    @Override
    public int getCounter(String key) {
        return counters.getOrDefault(key, 0);
    }

    @Override
    public void incrementCounter(String key) {
        counters.put(key, getCounter(key) + 1);
    }

    @Override
    public void resetCounter(String key) {
        counters.put(key, 0);
    }

}
