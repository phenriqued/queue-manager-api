package phenriqued.github.queue_manager_api.controller.customer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import phenriqued.github.queue_manager_api.dto.customer.CreateCustomerDTO;
import phenriqued.github.queue_manager_api.dto.customer.CustomerCreatedResponseDTO;
import phenriqued.github.queue_manager_api.dto.customer.CustomerResponseDTO;
import phenriqued.github.queue_manager_api.dto.customer.UpdateCustomerDTO;
import phenriqued.github.queue_manager_api.model.customer.CustomerEntity;
import phenriqued.github.queue_manager_api.service.customer.CustomerService;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@TestPropertySource(locations = "classpath:application-test.properties")
class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JacksonTester<CreateCustomerDTO> requestCreateCustomer;
    @Autowired
    private JacksonTester<UpdateCustomerDTO> requestUpdateCustomer;
    @MockitoBean
    private CustomerService customerService;


    @Test
    @DisplayName("It should create a client when the data is correct, returning 201 created")
    void shouldCreateCustomerWhenDataIsCorrectReturn201() throws Exception{
        var requestJson = requestCreateCustomer.write(
                        new CreateCustomerDTO("Marta", "37729817128", "11999996688", LocalDate.of(2002, 5, 23)))
                .getJson();

        var respostaMockada = new CustomerCreatedResponseDTO(
                1L, "Marta", "37729817128", "11999996688",
                LocalDate.of(2002, 5, 23), false, LocalDateTime.now());
        when(customerService.createCustomer(any(CreateCustomerDTO.class))).thenReturn(respostaMockada);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Marta"))
                .andExpect(jsonPath("$.cpf").value("37729817128"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @DisplayName("It should not create a client when the CPF is invalid and returns a 404 error.")
    void shouldNotCreateCustomerWhenCPFIsIncorrectReturn404() throws Exception{
        var requestJsonWithIncorrectCPF = requestCreateCustomer.write(
                        new CreateCustomerDTO("Marta", "123456789", "11999996688", LocalDate.of(2002, 5, 23)))
                .getJson();

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonWithIncorrectCPF))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fieldError").value("cpf"))
                .andExpect(jsonPath("$[0].defaultMessage").value("Invalid CPF"));
    }
    @Test
    @DisplayName("It should not create a client when the Phone Number format is invalid, as this returns a 404 error.")
    void shouldNotCreateCustomerWhenPhoneNumberIsIncorrectReturn404() throws Exception{
        var requestJsonWithIncorrectPhoneNumber = requestCreateCustomer.write(
                        new CreateCustomerDTO("Marta", "37729817128", "123456", LocalDate.of(2002, 5, 23)))
                .getJson();

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonWithIncorrectPhoneNumber))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fieldError").value("phoneNumber"))
                .andExpect(jsonPath("$[0].defaultMessage").value("The mobile phone number format is incorrect. Try this format: \"DD9XXXXXXXX\""));
    }
    @Test
    @DisplayName("It should not create a client when the Birthdate is in the past or future, as this returns a 404 error.")
    void shouldNotCreateCustomerWhenBirthDateIsPresentOrFutureReturn404() throws Exception{
        var requestJsonWithIncorrectPhoneNumber = requestCreateCustomer.write(
                        new CreateCustomerDTO("Marta", "37729817128", "11999996688", LocalDate.now()))
                .getJson();

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonWithIncorrectPhoneNumber))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fieldError").value("birthDate"))
                .andExpect(jsonPath("$[0].defaultMessage").value("must be a past date"));
    }

    @Test
    @DisplayName("It should return JSON containing a customer's data by ID")
    void shouldReturnJsonContainingCustomerDataById() throws Exception {
        Long idCpf = 1L;
        var respostaMockada = new CustomerResponseDTO(
                idCpf, "Marta", "37729817128", "11999996688",
                LocalDate.of(2002, 5, 23), false, LocalDateTime.now());

        when(customerService.getCustomerById(any())).thenReturn(respostaMockada);

        mockMvc.perform(get("/customers/" + idCpf))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Marta"))
                .andExpect(jsonPath("$.cpf").value("37729817128"))
                .andExpect(jsonPath("$.id").value(idCpf))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @DisplayName("should update customer data, as this returns a 203 no content.")
    void shouldUpdateCustomerData() throws Exception {
        var requestJson = requestUpdateCustomer.write(new UpdateCustomerDTO(null, "11999999999")).getJson();

        Long idCpf = 1L;
        var respostaMockada = new CustomerEntity(new CreateCustomerDTO("Marta", "37729817128", "11999996688",
                LocalDate.of(2002, 5, 23)));

        when(customerService.findCustomerById(any())).thenReturn(respostaMockada);

        mockMvc.perform(patch("/customers/" + idCpf)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNoContent());
    }
    @Test
    @DisplayName("It should delete a client, as this returns a 203 no content")
    void shouldDeleteCustomer() throws Exception {
        Long idCpf = 1L;
        var respostaMockada = new CustomerEntity(new CreateCustomerDTO("Marta", "37729817128", "11999996688",
                LocalDate.of(2002, 5, 23)));

        when(customerService.findCustomerById(any())).thenReturn(respostaMockada);

        mockMvc.perform(delete("/customers/" + idCpf))
                .andExpect(status().isNoContent());
    }


}