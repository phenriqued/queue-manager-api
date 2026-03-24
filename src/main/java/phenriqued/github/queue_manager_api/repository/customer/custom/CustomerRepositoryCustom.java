package phenriqued.github.queue_manager_api.repository.customer.custom;

import phenriqued.github.queue_manager_api.model.customer.CustomerEntity;
import phenriqued.github.queue_manager_api.repository.customer.custom.param.CustomerFilterParams;

import java.util.List;

public interface CustomerRepositoryCustom {

    List<CustomerEntity> getWithFilter(CustomerFilterParams customerFilterParams);

}
