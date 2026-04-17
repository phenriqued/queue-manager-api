package phenriqued.github.queue_manager_api.infra.exception.custom;

public class InvalidQueueTransitionException extends RuntimeException {
    public InvalidQueueTransitionException(String message) {
        super(message);
    }
}
