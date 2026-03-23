package phenriqued.github.queue_manager_api.Repositories.CustomerRepository.CustomerCustom;

import phenriqued.github.queue_manager_api.Models.Customer.CustomerEntity;
import phenriqued.github.queue_manager_api.Repositories.CustomerRepository.CustomerCustom.Param.CustomerFilterParams;

import java.util.List;

public interface CustomerRepositoryCustom {

    List<CustomerEntity> getWithFilter(CustomerFilterParams customerFilterParams);

}
