package phenriqued.github.queue_manager_api.model.customer;

import br.com.caelum.stella.validation.CPFValidator;
import jakarta.persistence.*;
import lombok.*;
import phenriqued.github.queue_manager_api.dto.customer.CreateCustomerDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "tb_customer")

@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter @NonNull
    @Column(nullable = false)
    private String name;
    @Column(length = 11, nullable = false, unique = true)
    private String cpf;
    @Setter @NonNull
    @Column(length = 11, nullable = false, unique = true)
    private String phoneNumber;
    @Column(nullable = false)
    private LocalDate birthDate;
    @Column(nullable = false)
    private Boolean isPriority;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public CustomerEntity(CreateCustomerDTO customerDTO) {
        this.name = customerDTO.name();
        this.cpf = customerDTO.cpf();
        this.phoneNumber = customerDTO.phoneNumber();
        this.birthDate = customerDTO.birthDate();
        this.isPriority = isElderly();
        this.createdAt = LocalDateTime.now();
    }

    private Boolean isElderly(){
        return ChronoUnit.YEARS.between(this.birthDate, LocalDate.now()) >= 60;
    }


}
