package phenriqued.github.queue_manager_api.controller.ticket;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import phenriqued.github.queue_manager_api.dto.ticket.TicketRequestDTO;
import phenriqued.github.queue_manager_api.dto.ticket.TicketResponseDTO;
import phenriqued.github.queue_manager_api.dto.ticket.TicketUpdateRequestDTO;
import phenriqued.github.queue_manager_api.model.ticket.TicketStatus;
import phenriqued.github.queue_manager_api.model.ticket.TypeTicket;
import phenriqued.github.queue_manager_api.service.ticket.TicketService;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@TestPropertySource(locations = "classpath:application-test.properties")
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JacksonTester<TicketRequestDTO> requestTicketDTO;
    @Autowired
    private JacksonTester<TicketUpdateRequestDTO> requestUpdateTicketDTO;
    @MockitoBean
    private TicketService ticketService;


    @Test
    @DisplayName("A ticket should be created even when the customer is not added, but added if it is a priority")
    void shouldCreateTicketWhenDataIsCorrectReturn201() throws Exception{
        var requestJson = requestTicketDTO.write(new TicketRequestDTO(null, true, 1L)).getJson();

        var responsaMockada = new TicketResponseDTO(1L, null, "P-10001",
                TypeTicket.PRIORITY, TicketStatus.PENDING, LocalDateTime.now(), "Atendimento Geral", null, null);
        when(ticketService.issueTicket(any(TicketRequestDTO.class))).thenReturn(responsaMockada);

        mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.code").value("P-10001"))
                .andExpect(jsonPath("$.typeTicket").value("PRIORITY"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.queueName").value("Atendimento Geral"));
    }
    @Test
    @DisplayName("A ticket should not be created if no customers have been added, or if neither a priority nor a queue ID has been added.")
    void shouldNotCreateTicketWhenDataIsIncorrectReturn404() throws Exception{
        var requestIncorrectJson = requestTicketDTO.write(new TicketRequestDTO(null, null, null)).getJson();

        mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestIncorrectJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(1)));
    }
    @Test
    @DisplayName("It should return JSON containing a ticket's data by ID")
    void shouldReturnJsonContainingTicketDataById() throws Exception {
        Long ticketId = 1L;
        var responsaMockada = new TicketResponseDTO(1L, null, "P-10001",
                TypeTicket.PRIORITY, TicketStatus.PENDING, LocalDateTime.now(), "Atendimento Geral", null, null);
        when(ticketService.findById(any(Long.class))).thenReturn(responsaMockada);

        mockMvc.perform(get("/tickets/" + ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.code").value("P-10001"))
                .andExpect(jsonPath("$.typeTicket").value("PRIORITY"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.queueName").value("Atendimento Geral"));
    }

    @Test
    @DisplayName("should update CPF or the queue for a ticket, as this returns a 203 no content.")
    void shouldUpdateCPFOrTheQueueForTicket() throws Exception {
        var requestUpdateJson = requestUpdateTicketDTO.write(new TicketUpdateRequestDTO("37729817128", null)).getJson();

        Long idTicket = 1L;

        mockMvc.perform(patch("/tickets/"+idTicket)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestUpdateJson))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("It should delete a client, as this returns a 203 no content")
    void shouldDeleteTicket() throws Exception {
        Long idTicket = 1L;
        mockMvc.perform(delete("/tickets/"+idTicket))
                .andExpect(status().isNoContent());
    }



}