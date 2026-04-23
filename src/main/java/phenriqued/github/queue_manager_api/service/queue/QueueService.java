package phenriqued.github.queue_manager_api.service.queue;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phenriqued.github.queue_manager_api.dto.queue.QueueResponseDTO;
import phenriqued.github.queue_manager_api.dto.ticket.TicketResponseDTO;
import phenriqued.github.queue_manager_api.infra.exception.custom.InvalidQueueTransitionException;
import phenriqued.github.queue_manager_api.infra.exception.custom.NoTicketInQueueException;
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

    public void addToQueue(@NotNull TicketEntity ticket){
        if (Objects.isNull(ticket.getQueue())) throw new InvalidQueueTransitionException("It's not possible to add the ticket to the queue when the queue is null. Check the ticket's dependency.");
        QueueEntity queue = ticket.getQueue();
        queueState.putIfAbsent(queue.getId(), new InMemoryQueueState(queue.getId()));
        queueState.get(queue.getId()).addTicketToQueue(ticket);
    }

    @Transactional
    public TicketResponseDTO callNext(Long id){
        QueueEntity queue = findQueueById(id);
        InMemoryQueueState memoryQueue = queueState.get(queue.getId());
        if (memoryQueue == null) throw new EntityNotFoundException("Queue not initialized or Entity not found!");

        TicketEntity ticket = null;

        memoryQueue.getLock().lock();
        try {
            if (memoryQueue.getPreferentialCalledCount() >= 2 && !memoryQueue.getNormalQueue().isEmpty()) {
                memoryQueue.resetPreferencialCalledCount();
                ticket = memoryQueue.pollNormalQueue();
            } else if (!memoryQueue.getPreferentialQueue().isEmpty()) {
                memoryQueue.incrementPreferencialCalledCount();
                ticket = memoryQueue.pollPreferentialQueue();
            } else if (!memoryQueue.getNormalQueue().isEmpty()) {
                memoryQueue.resetPreferencialCalledCount();
                ticket = memoryQueue.pollNormalQueue();
            }
            if (ticket == null) {
                throw new NoTicketInQueueException("There are no more tickets in the queue.");
            }
            ticket.startAttendance();
        }finally {
            memoryQueue.getLock().unlock();
        }
        ticketRepository.save(ticket);
        return new TicketResponseDTO(ticket);
    }

    public QueueEntity findQueueById(Long id) {
        if(Objects.isNull(id)) throw new InvalidQueueTransitionException("It is not possible to search for a queue whose ID is equal to null.");
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

    public boolean removeTicketFromQueue(@NotNull TicketEntity ticket){
        if (Objects.isNull(ticket.getQueue())) throw new InvalidQueueTransitionException("It's not possible to remove the ticket to the queue when the queue is null. Check the ticket's dependency.");
        QueueEntity queue = ticket.getQueue();
        InMemoryQueueState memoryQueue = queueState.get(queue.getId());
        return memoryQueue.removeTicketQueue(ticket);
    }

}
