package phenriqued.github.queue_manager_api.repository.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import phenriqued.github.queue_manager_api.model.customer.CustomerEntity;
import phenriqued.github.queue_manager_api.repository.customer.custom.CustomerRepositoryCustom;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long>, CustomerRepositoryCustom {

    Optional<CustomerEntity> findByCpf(String cpf);

}
