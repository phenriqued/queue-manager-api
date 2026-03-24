package phenriqued.github.queue_manager_api.dto.customer;

import phenriqued.github.queue_manager_api.model.customer.CustomerEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CustomerCreatedResponseDTO(
        Long id,
        String name,
        String cpf,
        String phoneNumber,
        LocalDate birthDate,
        Boolean isPriority,
        LocalDateTime createdAt) {

    public CustomerCreatedResponseDTO(CustomerEntity customer){
        this(customer.getId(), customer.getName(), customer.getCpf(), customer.getPhoneNumber(),
                customer.getBirthDate(), customer.getIsPriority(), customer.getCreatedAt());
    }

}
