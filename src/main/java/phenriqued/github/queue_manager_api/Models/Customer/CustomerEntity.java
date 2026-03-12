package phenriqued.github.queue_manager_api.Models.Customer;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "tb_customer")

@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(length = 11, nullable = false, unique = true)
    private String cpf;
    @Column(length = 11, nullable = false, unique = true)
    private String phoneNumber;
    @Column(nullable = false)
    private LocalDate birthDate;
    @Column(nullable = false)
    private Boolean isPriority;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public CustomerEntity(String name, String cpf, String phoneNumber, LocalDate birthDate) {
        this.name = name;
        this.cpf = cpf;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.isPriority = isElderly();
        this.createdAt = LocalDateTime.now();
    }

    private Boolean isElderly(){
        return ChronoUnit.YEARS.between(this.birthDate, LocalDate.now()) >= 60;
    }


}
