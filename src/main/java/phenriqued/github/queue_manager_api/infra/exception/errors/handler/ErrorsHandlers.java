package phenriqued.github.queue_manager_api.infra.exception.errors.handler;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import phenriqued.github.queue_manager_api.dto.exception.DataErrorValidationDTO;
import phenriqued.github.queue_manager_api.infra.exception.custom.IllegalDataException;
import phenriqued.github.queue_manager_api.infra.exception.custom.NoTicketInQueueException;

import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class ErrorsHandlers {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handlerAllException(Exception e){
        System.out.println("Tracker:\n"+ Arrays.toString(e.getStackTrace()) +"\nDefault Message: "+e.getMessage()+"\nClass: "+e.getClass());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("[INTERNAL ERROR]");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DataErrorValidationDTO>> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e){
        var errors = e.getFieldErrors();
        return ResponseEntity.badRequest().body(
                errors.stream().map(DataErrorValidationDTO::new).toList());
    }

    @ExceptionHandler(IllegalDataException.class)
    public ResponseEntity<String> handlerIllegalDataException(IllegalDataException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handlerIllegalStateException(IllegalStateException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    @ExceptionHandler(NoTicketInQueueException.class)
    public ResponseEntity<String> handlerNoTicketInQueueException(NoTicketInQueueException e){
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handlerEntityNotFoundException(EntityNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handlerDataIntegrityViolationException(DataIntegrityViolationException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("[ERROR] "+e.getMessage());
    }
//HttpMessageNotReadableException

}
