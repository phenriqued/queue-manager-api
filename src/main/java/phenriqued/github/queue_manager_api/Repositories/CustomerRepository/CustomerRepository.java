package phenriqued.github.queue_manager_api.Repositories.CustomerRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import phenriqued.github.queue_manager_api.Models.Customer.CustomerEntity;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

}
