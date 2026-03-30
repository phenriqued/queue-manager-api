package phenriqued.github.queue_manager_api.service.queue;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phenriqued.github.queue_manager_api.model.queue.QueueEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketStatus;
import phenriqued.github.queue_manager_api.model.ticket.TypeTicket;
import phenriqued.github.queue_manager_api.repository.queue.QueueRepository;

import java.util.PriorityQueue;

@Service
public class QueueService {

    private final QueueRepository repository;
    private PriorityQueue<TicketEntity> preferentialQueue = new PriorityQueue<>();
    private PriorityQueue<TicketEntity> normalQueue = new PriorityQueue<>();

    public QueueService(QueueRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void addToQueue(TicketEntity ticket){
        QueueEntity queue = ticket.getQueue();
        queue.getTicketQueue().add(ticket);
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        if(ticket.getTypeTicket().equals(TypeTicket.PRIORITY)){
            preferentialQueue.add(ticket);
        }else {
            normalQueue.add(ticket);
        }
    }



    public QueueEntity findQueueById(Long id) {
        return repository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
