package phenriqued.github.queue_manager_api.service.customer;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import phenriqued.github.queue_manager_api.dto.customer.CreateCustomerDTO;
import phenriqued.github.queue_manager_api.dto.customer.CustomerCreatedResponseDTO;
import phenriqued.github.queue_manager_api.dto.customer.CustomerResponseDTO;
import phenriqued.github.queue_manager_api.dto.customer.UpdateCustomerDTO;
import phenriqued.github.queue_manager_api.model.customer.CustomerEntity;
import phenriqued.github.queue_manager_api.repository.customer.custom.param.CustomerFilterParams;
import phenriqued.github.queue_manager_api.repository.customer.CustomerRepository;

import java.util.List;


@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerCreatedResponseDTO createCustomer(CreateCustomerDTO data){
        var customer = new CustomerEntity(data);
        customerRepository.save(customer);
        return new CustomerCreatedResponseDTO(customer);
    }

    public CustomerResponseDTO getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(CustomerResponseDTO::new)
                .orElseThrow(() -> new EntityNotFoundException("Customer was not found!"));
    }
    public List<CustomerResponseDTO> getAllCustomers(CustomerFilterParams params){
        return customerRepository.getWithFilter(params).stream().map(CustomerResponseDTO::new).toList();
    }

    public void updateCustomer(Long id, UpdateCustomerDTO customerDTO){
        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer was not found!"));

        customer.setPhoneNumber(customer.getPhoneNumber());
        customer.setName(customer.getName());
    }

    public void deleteCustomer(Long id) {
        if(customerRepository.existsById(id)){
            customerRepository.deleteById(id);
            return;
        }
        throw new EntityNotFoundException("Customer was not found!");
    }
}
