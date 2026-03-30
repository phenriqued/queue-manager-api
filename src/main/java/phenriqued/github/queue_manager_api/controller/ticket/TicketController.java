package phenriqued.github.queue_manager_api.controller.ticket;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import phenriqued.github.queue_manager_api.dto.ticket.TicketRequestDTO;
import phenriqued.github.queue_manager_api.dto.ticket.TicketResponseDTO;
import phenriqued.github.queue_manager_api.dto.ticket.TicketUpdateRequestDTO;
import phenriqued.github.queue_manager_api.service.ticket.TicketService;

import java.net.URI;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TicketResponseDTO> createTicket(@RequestBody @Valid TicketRequestDTO request){
        TicketResponseDTO ticket = service.issueTicket(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("{id}").buildAndExpand(ticket.createdAt()).toUri();
        return ResponseEntity.created(uri).body(ticket);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> getTicketById(@PathVariable Long id){
        return ResponseEntity.ok().body(service.findById(id));
    }
    @GetMapping
    public ResponseEntity<Page<TicketResponseDTO>> getAllTickets(@PageableDefault(size = 15, sort = "code") Pageable pageable){
        return ResponseEntity.ok().body(service.findAllTickets(pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateTicket(@PathVariable Long id, @RequestBody @Valid TicketUpdateRequestDTO updateRequestDTO){
        service.updateTicket(id, updateRequestDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketById(@PathVariable Long id){
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }



}
