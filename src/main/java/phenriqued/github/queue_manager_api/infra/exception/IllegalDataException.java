package phenriqued.github.queue_manager_api.infra.exception;

public class IllegalDataException extends RuntimeException {
    public IllegalDataException(String message) {
        super(message);
    }

    public IllegalDataException() {}
}
