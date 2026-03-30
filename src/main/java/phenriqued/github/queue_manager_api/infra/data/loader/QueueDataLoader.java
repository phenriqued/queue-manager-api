package phenriqued.github.queue_manager_api.infra.data.loader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import phenriqued.github.queue_manager_api.model.queue.QueueEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketStatus;
import phenriqued.github.queue_manager_api.repository.queue.QueueRepository;
import phenriqued.github.queue_manager_api.repository.ticket.TicketRepository;
import phenriqued.github.queue_manager_api.service.queue.QueueService;

import java.util.List;

@Component
public class QueueDataLoader implements CommandLineRunner {

    private final QueueService queueService;
    private final QueueRepository repository;
    private final TicketRepository ticketRepository;

    public QueueDataLoader(QueueRepository repository, TicketRepository ticketRepository, QueueService queueService) {
        this.repository = repository;
        this.ticketRepository = ticketRepository;
        this.queueService = queueService;
    }


    @Override
    public void run(String... args) throws Exception {
        saveQueue();
        addAllTicketsInPending();
    }

    private void saveQueue(){
        repository.findById(1L)
                .ifPresentOrElse(
                        queues -> System.out.println("[INFO] Queues was registered"),
                        () -> {
                            repository.save(new QueueEntity("Atendimento Geral"));
                            repository.save(new QueueEntity("Caixa"));
                            repository.save(new QueueEntity("Triagem"));
                        }
                );
    }

    public void addAllTicketsInPending(){
        repository.findAll().forEach(
                queue -> addAllTicketsInPendingByQueue(queue.getId()));
    }


    private void addAllTicketsInPendingByQueue(Long id){
        List<TicketEntity> ticketsInProgress = ticketRepository.findAllByIdAndStatus(id, TicketStatus.PENDING);

        if(!ticketsInProgress.isEmpty()){
            ticketsInProgress.forEach(queueService::addToQueue);
        }
        System.out.println("[INFO] "+ ticketsInProgress.size() + " tickets added to the queue.");
    }

}
