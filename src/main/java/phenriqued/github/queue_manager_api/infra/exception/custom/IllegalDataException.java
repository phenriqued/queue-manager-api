package phenriqued.github.queue_manager_api.infra.exception.custom;

public class IllegalDataException extends RuntimeException {
    public IllegalDataException(String message) {
        super(message);
    }

    public IllegalDataException() {}
}
