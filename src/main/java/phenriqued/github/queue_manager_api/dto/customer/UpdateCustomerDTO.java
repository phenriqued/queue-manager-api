package phenriqued.github.queue_manager_api.dto.customer;

import jakarta.validation.constraints.Pattern;

public record UpdateCustomerDTO(
        String name,
        @Pattern(regexp = "^[1-9]{2}9[0-9]{8}$",
                message = "The mobile phone number format is incorrect. Try this format: \"DD9XXXXXXXX\"")
        String phoneNumber) {

}
