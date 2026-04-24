package phenriqued.github.queue_manager_api.service.customer;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import phenriqued.github.queue_manager_api.dto.customer.CreateCustomerDTO;
import phenriqued.github.queue_manager_api.dto.customer.UpdateCustomerDTO;
import phenriqued.github.queue_manager_api.model.customer.CustomerEntity;
import phenriqued.github.queue_manager_api.repository.customer.CustomerRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerRepository customerRepository;

    @BeforeAll
    @Transactional
    void setup(){
        List<CustomerEntity> entityList = List.of(
                new CustomerEntity(
                        new CreateCustomerDTO("Teste", "84270873221", "11999999999", LocalDate.of(1946, 5, 23))),
                new CustomerEntity(
                        new CreateCustomerDTO("Teste 2", "18469150073", "11888888888", LocalDate.of(1950, 5, 23)))
        );
        customerRepository.saveAll(entityList);
    }

    @Test
    @DisplayName("It should create a client when the data is correct.")
    void shouldCreateCustomer() {
        CreateCustomerDTO customerDTO = new CreateCustomerDTO("Maria", "16406384318", "11998877665", LocalDate.of(2002, 5, 23));

        customerService.createCustomer(customerDTO);
        var customer = customerRepository.findByCpf("16406384318").orElse(null);

        assertNotNull(customer);
        assertEquals("Maria", customer.getName());
        assertEquals("11998877665", customer.getPhoneNumber());
        assertFalse(customer.getIsPriority());
    }

    @Test
    @DisplayName("should update the customer data")
    @Transactional
    void updateCustomer() {
        UpdateCustomerDTO updateCustomer = new UpdateCustomerDTO("João", "11975712345");

        customerService.updateCustomer(2L, updateCustomer);
        var customerUpdated = customerRepository.findByCpf("18469150073").orElse(null);

        assertNotNull(customerUpdated);
        assertEquals("João", customerUpdated.getName());
        assertEquals("11975712345", customerUpdated.getPhoneNumber());
    }

    @Test
    @DisplayName("should delete an existing customer")
    void deleteCustomer() {
        customerService.deleteCustomer(1L);

        var customerDeleted = customerRepository.findByCpf("84270873221").orElse(null);

        assertNull(customerDeleted);
    }
    @Test
    @DisplayName("should not delete a non-existent customer.")
    void shouldNotDeleteCustomer() {
        Exception exception = assertThrows(EntityNotFoundException.class, () -> customerService.deleteCustomer(9999L));
        assertEquals("Customer was not found!", exception.getMessage());
    }

}