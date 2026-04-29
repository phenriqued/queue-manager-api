package phenriqued.github.queue_manager_api.controller.queue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import phenriqued.github.queue_manager_api.dto.ticket.TicketResponseDTO;
import phenriqued.github.queue_manager_api.infra.exception.custom.NoTicketInQueueException;
import phenriqued.github.queue_manager_api.model.ticket.TicketStatus;
import phenriqued.github.queue_manager_api.model.ticket.TypeTicket;
import phenriqued.github.queue_manager_api.service.queue.QueueService;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class QueueControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private QueueService queueService;

    @Test
    @DisplayName("It should return status 200 OK, returning the ticket that is in the queue.")
    void callNextTeste() throws Exception{
        Long idQueue = 1L;
        var responseMock = new TicketResponseDTO(1L, null, "P-10001",
                TypeTicket.PRIORITY, TicketStatus.PENDING, LocalDateTime.now(), "Atendimento Geral", null, null);
        when(queueService.callNext(idQueue)).thenReturn(responseMock);

        mockMvc.perform(post("/queue/"+idQueue+"/next"))
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
    @DisplayName("It should return status 201 no content, when there is no ticket in the queue.")
    void shouldReturnStatus201NoContentWhenNoTicketInQueue() throws Exception{
        Long idQueue = 1L;

        when(queueService.callNext(idQueue)).thenThrow(new NoTicketInQueueException("There are no more tickets in the queue."));

        mockMvc.perform(post("/queue/"+idQueue+"/next"))
                .andExpect(status().isNoContent());
    }
}