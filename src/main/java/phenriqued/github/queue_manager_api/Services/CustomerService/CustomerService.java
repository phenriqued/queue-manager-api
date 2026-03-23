package phenriqued.github.queue_manager_api.Services.CustomerService;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import phenriqued.github.queue_manager_api.DTOs.CustomerDTO.CreateCustomerDTO;
import phenriqued.github.queue_manager_api.DTOs.CustomerDTO.CustomerCreatedResponseDTO;
import phenriqued.github.queue_manager_api.DTOs.CustomerDTO.CustomerResponseDTO;
import phenriqued.github.queue_manager_api.DTOs.CustomerDTO.UpdateCustomerDTO;
import phenriqued.github.queue_manager_api.Models.Customer.CustomerEntity;
import phenriqued.github.queue_manager_api.Repositories.CustomerRepository.CustomerCustom.Param.CustomerFilterParams;
import phenriqued.github.queue_manager_api.Repositories.CustomerRepository.CustomerRepository;

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
                .orElseThrow(EntityNotFoundException::new);
    }
    public List<CustomerResponseDTO> getAllCustomers(CustomerFilterParams params){
        return customerRepository.getWithFilter(params).stream().map(CustomerResponseDTO::new).toList();
    }
    public Page<CustomerResponseDTO> getAllCustomers(Pageable pageable){
        return customerRepository.findAll(pageable)
                .map(CustomerResponseDTO::new);
    }

    public void updateCustomer(Long id, UpdateCustomerDTO customerDTO){
        CustomerEntity customer = customerRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        customer.setPhoneNumber(customer.getPhoneNumber());
        customer.setName(customer.getName());
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}
