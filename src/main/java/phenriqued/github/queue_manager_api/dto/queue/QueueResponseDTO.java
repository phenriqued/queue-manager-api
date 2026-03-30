package phenriqued.github.queue_manager_api.dto.queue;

import phenriqued.github.queue_manager_api.model.queue.QueueEntity;

public record QueueResponseDTO(
        Long id,
        String name) {

    public QueueResponseDTO(QueueEntity entity){
        this(entity.getId(), entity.getNameQueue());
    }
}
