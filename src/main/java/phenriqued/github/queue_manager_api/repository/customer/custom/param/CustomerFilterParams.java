package phenriqued.github.queue_manager_api.repository.customer.custom.param;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerFilterParams {

    private String name;
    private String cpf;
    private String phoneNumber;

    private LocalDate birthDate;
    private Boolean isPriority;

}
