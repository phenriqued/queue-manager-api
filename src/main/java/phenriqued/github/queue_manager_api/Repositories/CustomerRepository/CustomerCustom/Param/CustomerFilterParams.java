package phenriqued.github.queue_manager_api.Repositories.CustomerRepository.CustomerCustom.Param;

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
