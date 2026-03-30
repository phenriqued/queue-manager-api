package phenriqued.github.queue_manager_api.infra.data.loader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import phenriqued.github.queue_manager_api.model.queue.QueueEntity;
import phenriqued.github.queue_manager_api.repository.queue.QueueRepository;

@Component
public class QueueDataLoader implements CommandLineRunner {

    private final QueueRepository repository;
    public QueueDataLoader(QueueRepository repository) {
        this.repository = repository;
    }


    @Override
    public void run(String... args) throws Exception {
        saveQueue();
    }

    private void saveQueue(){
        repository.findByNameQueue("Atendimento Geral")
                .ifPresentOrElse(
                        queues -> System.out.println("[INFO] Queues was registered"),
                        () -> {
                            repository.save(new QueueEntity("Atendimento Geral"));
                            repository.save(new QueueEntity("Caixa"));
                            repository.save(new QueueEntity("Triagem"));
                        }
                );
    }

}
