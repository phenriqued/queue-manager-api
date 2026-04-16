package phenriqued.github.queue_manager_api.infra.exception.custom;

public class InvalidTicketOperationException extends RuntimeException {
    public InvalidTicketOperationException(String message) {
        super(message);
    }
}
