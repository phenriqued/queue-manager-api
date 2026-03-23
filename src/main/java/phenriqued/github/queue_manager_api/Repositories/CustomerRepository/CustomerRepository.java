package phenriqued.github.queue_manager_api.Repositories.CustomerRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import phenriqued.github.queue_manager_api.Models.Customer.CustomerEntity;
import phenriqued.github.queue_manager_api.Repositories.CustomerRepository.CustomerCustom.CustomerRepositoryCustom;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long>, CustomerRepositoryCustom {

    Optional<CustomerEntity> findByCpf(String cpf);

}
