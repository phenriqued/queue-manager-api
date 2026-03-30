package phenriqued.github.queue_manager_api.repository.queue;

import org.springframework.data.jpa.repository.JpaRepository;
import phenriqued.github.queue_manager_api.model.queue.QueueEntity;

import java.util.Optional;

public interface QueueRepository extends JpaRepository<QueueEntity, Long> {

    Optional<QueueEntity> findByNameQueue(String nameQueue);

}
