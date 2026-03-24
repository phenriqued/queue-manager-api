package phenriqued.github.queue_manager_api.controller.customer;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import phenriqued.github.queue_manager_api.dto.customer.CreateCustomerDTO;
import phenriqued.github.queue_manager_api.dto.customer.CustomerCreatedResponseDTO;
import phenriqued.github.queue_manager_api.dto.customer.CustomerResponseDTO;
import phenriqued.github.queue_manager_api.dto.customer.UpdateCustomerDTO;
import phenriqued.github.queue_manager_api.repository.customer.custom.param.CustomerFilterParams;
import phenriqued.github.queue_manager_api.service.customer.CustomerService;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerCreatedResponseDTO> createCustomer(@RequestBody @Valid CreateCustomerDTO createCustomer){
        var customerData = customerService.createCustomer(createCustomer);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(customerData.id()).toUri();
        return ResponseEntity.created(uri).body(customerData);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers(CustomerFilterParams params){
        return ResponseEntity.ok(customerService.getAllCustomers(params));
    }
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id){
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateCustomers(@PathVariable Long id, @RequestBody @Valid UpdateCustomerDTO updateCustomer){
        customerService.updateCustomer(id, updateCustomer);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id){
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }



}
