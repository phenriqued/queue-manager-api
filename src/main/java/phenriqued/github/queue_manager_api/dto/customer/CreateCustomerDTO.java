package phenriqued.github.queue_manager_api.dto.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import phenriqued.github.queue_manager_api.model.customer.CustomerEntity;

import java.time.LocalDate;

public record CreateCustomerDTO(
        @NotBlank
        String name,
        @NotBlank
        @Size(min = 11, max = 11)
        String cpf,
        @Pattern(regexp = "^[1-9]{2}\\s?9[0-9]{8}$")
        @Size(min = 11, max = 11)
        String phoneNumber,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "[dd/MM/yyyy][yyyy-MM-dd]")
        @Past
        LocalDate birthDate) {

    public CreateCustomerDTO{
        if(!CustomerEntity.isValidCPF(cpf)) throw new IllegalArgumentException("Invalid CPF");
    }

}
