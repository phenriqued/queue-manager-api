package phenriqued.github.queue_manager_api.DTOs.CustomerDTO;

import java.util.Objects;

public record UpdateCustomerDTO(
        String name,
        String phoneNumber) {

    public UpdateCustomerDTO{
        if(Objects.nonNull(phoneNumber)){
            if(!phoneNumber.matches("^[1-9]{2}\\s?9[0-9]{8}$"))
                throw new IllegalArgumentException("The mobile phone number format is incorrect. \nTry this format: \"DD 9XXXXXXXX\"");
        }
    }

}
