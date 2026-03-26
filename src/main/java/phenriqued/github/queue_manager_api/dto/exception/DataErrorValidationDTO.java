package phenriqued.github.queue_manager_api.dto.exception;

import org.springframework.validation.FieldError;

public record DataErrorValidationDTO(
        String fieldError,
        String defaultMessage) {

    public DataErrorValidationDTO(FieldError error){
        this(error.getField(), error.getDefaultMessage());
    }

}
