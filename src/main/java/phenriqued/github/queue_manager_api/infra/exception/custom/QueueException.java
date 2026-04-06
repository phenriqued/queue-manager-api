package phenriqued.github.queue_manager_api.infra.exception.custom;

public class QueueException extends RuntimeException {
    public QueueException(String message) {
        super(message);
    }
}
