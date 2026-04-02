package phenriqued.github.queue_manager_api.service.queue;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phenriqued.github.queue_manager_api.dto.queue.QueueResponseDTO;
import phenriqued.github.queue_manager_api.dto.ticket.TicketResponseDTO;
import phenriqued.github.queue_manager_api.model.queue.QueueEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketStatus;
import phenriqued.github.queue_manager_api.repository.queue.QueueRepository;
import phenriqued.github.queue_manager_api.repository.ticket.TicketRepository;
import phenriqued.github.queue_manager_api.service.queue.utils.InMemoryQueueState;

import java.util.*;

@Service
public class QueueService {

    private final QueueRepository repository;
    private final TicketRepository ticketRepository;
    private Map<Long, InMemoryQueueState> queueState = new HashMap<>();

    public QueueService(QueueRepository repository, TicketRepository ticketRepository) {
        this.repository = repository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public void addToQueue(TicketEntity ticket){
        QueueEntity queue = ticket.getQueue();
        queueState.putIfAbsent(queue.getId(), new InMemoryQueueState(queue.getId()));
        queueState.get(queue.getId()).addTicketToQueue(ticket);
    }

    @Transactional
    public TicketResponseDTO callNext(Long id){
        QueueEntity queue = findQueueById(id);
        InMemoryQueueState memoryQueue = queueState.get(queue.getId());
        TicketEntity ticket = new TicketEntity();
        if (queue.getPreferentialCalledCount() >= 2 && !memoryQueue.getNormalQueue().isEmpty()){
            queue.setPreferentialCalledCount(0);
            memoryQueue.setPreferentialCalledCount(0);
            ticket = memoryQueue.pollNormalQueue();
        }else if(!memoryQueue.getPreferentialQueue().isEmpty()){
            queue.setPreferentialCalledCount(queue.getPreferentialCalledCount() + 1);
            memoryQueue.setPreferentialCalledCount(memoryQueue.getPreferentialCalledCount() + 1);
            ticket = memoryQueue.pollPreferentialQueue();
        }else if (!memoryQueue.getNormalQueue().isEmpty()){
            queue.setPreferentialCalledCount(0);
            memoryQueue.setPreferentialCalledCount(0);
            ticket = memoryQueue.pollNormalQueue();
        }
        ticket.startAttendance();
        ticketRepository.saveAndFlush(ticket);
        return new TicketResponseDTO(ticket);
    }

    public QueueEntity findQueueById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Queue was not found!"));
    }

    public int getSize(Long id) {
        return Math.toIntExact(
                ticketRepository.countByQueueIdAndStatus(id, TicketStatus.PENDING));
    }

    public List<QueueResponseDTO> getAllQueues() {
        return repository.findAll().stream().map(QueueResponseDTO::new).toList();
    }

    public boolean removeTicketFromQueue(TicketEntity ticket){
        QueueEntity queue = ticket.getQueue();
        InMemoryQueueState memoryQueue = queueState.get(queue.getId());
        return memoryQueue.removeTicketQueue(ticket);
    }

}
