package phenriqued.github.queue_manager_api.service.queue.utils;

import lombok.Getter;
import lombok.Setter;
import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;
import phenriqued.github.queue_manager_api.model.ticket.TypeTicket;

import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantLock;

@Getter
public class InMemoryQueueState {

    private Long queueId;
    private PriorityQueue<TicketEntity> preferentialQueue = new PriorityQueue<>();
    private PriorityQueue<TicketEntity> normalQueue = new PriorityQueue<>();
    private int preferentialCalledCount = 0;
    private final ReentrantLock lock = new ReentrantLock(true);

    public InMemoryQueueState(Long queueId) {
        this.queueId = queueId;
    }

    public void addTicketToQueue(TicketEntity ticket){
        if (ticket.getTypeTicket() == TypeTicket.PRIORITY){
            this.preferentialQueue.add(ticket);
        }else {
            this.normalQueue.add(ticket);
        }
    }
    public TicketEntity pollPreferentialQueue(){
        return this.preferentialQueue.poll();
    }
    public TicketEntity pollNormalQueue(){
        return this.normalQueue.poll();
    }
    public boolean removeTicketQueue(TicketEntity ticket){
        if (ticket.getTypeTicket() == TypeTicket.PRIORITY){
            return preferentialQueue.remove(ticket);
        }
        return normalQueue.remove(ticket);
    }
    public void incrementPreferencialCalledCount(){
        this.preferentialCalledCount++;
    }
    public void resetPreferencialCalledCount(){
        this.preferentialCalledCount = 0;
    }

}
