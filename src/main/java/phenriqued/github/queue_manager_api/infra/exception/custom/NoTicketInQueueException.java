package phenriqued.github.queue_manager_api.infra.exception.custom;

public class NoTicketInQueueException extends RuntimeException {
    public NoTicketInQueueException(String message) {
        super(message);
    }
}
