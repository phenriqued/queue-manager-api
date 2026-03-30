package phenriqued.github.queue_manager_api.service.queue;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phenriqued.github.queue_manager_api.dto.queue.QueueResponseDTO;
import phenriqued.github.queue_manager_api.dto.ticket.TicketResponseDTO;
import phenriqued.github.queue_manager_api.model.queue.QueueEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketStatus;
import phenriqued.github.queue_manager_api.model.ticket.TypeTicket;
import phenriqued.github.queue_manager_api.repository.queue.QueueRepository;
import phenriqued.github.queue_manager_api.repository.ticket.TicketRepository;

import java.util.List;
import java.util.PriorityQueue;

@Service
public class QueueService {

    private final QueueRepository repository;
    private final TicketRepository ticketRepository;

    private PriorityQueue<TicketEntity> preferentialQueue = new PriorityQueue<>();
    private PriorityQueue<TicketEntity> normalQueue = new PriorityQueue<>();

    public QueueService(QueueRepository repository, TicketRepository ticketRepository) {
        this.repository = repository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public void addToQueue(TicketEntity ticket){
        QueueEntity queue = ticket.getQueue();
        queue.getTicketQueue().add(ticket);
        if(ticket.getTypeTicket().equals(TypeTicket.PRIORITY)){
            preferentialQueue.add(ticket);
        }else {
            normalQueue.add(ticket);
        }
    }

    public TicketResponseDTO callNext(Long id){
        QueueEntity queue = findQueueById(id);
        if (queue.getPreferentialCalledCount() >= 2 && !normalQueue.isEmpty()){
            queue.setPreferentialCalledCount(0);
            return new TicketResponseDTO(normalQueue.poll());
        }
        if(!preferentialQueue.isEmpty()){
            queue.setPreferentialCalledCount(queue.getPreferentialCalledCount() + 1);
            return new TicketResponseDTO(preferentialQueue.poll());
        }
        if (!normalQueue.isEmpty()){
            queue.setPreferentialCalledCount(0);
            return new TicketResponseDTO(normalQueue.poll());
        }
        return null;
    }

    public QueueEntity findQueueById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Queue was not found!"));
    }

    public long getSize(Long id) {
        return ticketRepository.countByQueueIdAndStatus(id, TicketStatus.PENDING);
    }

    public List<QueueResponseDTO> getAllQueues() {
        return repository.findAll().stream().map(QueueResponseDTO::new).toList();
    }
}
