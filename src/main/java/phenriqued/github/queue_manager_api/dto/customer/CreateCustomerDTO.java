package phenriqued.github.queue_manager_api.dto.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import phenriqued.github.queue_manager_api.infra.validation.cpf.ValidCPF;

import java.time.LocalDate;

public record CreateCustomerDTO(
        @NotBlank
        String name,
        @NotBlank @NotNull
        @ValidCPF
        String cpf,
        @NotBlank @NotNull
        @Pattern(regexp = "^[1-9]{2}9[0-9]{8}$", message = "The mobile phone number format is incorrect. Try this format: \"DD9XXXXXXXX\"")
        String phoneNumber,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "[dd/MM/yyyy][yyyy-MM-dd]")
        @Past
        LocalDate birthDate) {

}
