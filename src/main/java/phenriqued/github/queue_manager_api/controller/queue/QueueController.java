package phenriqued.github.queue_manager_api.controller.queue;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import phenriqued.github.queue_manager_api.dto.queue.QueueResponseDTO;
import phenriqued.github.queue_manager_api.dto.ticket.TicketResponseDTO;
import phenriqued.github.queue_manager_api.service.queue.QueueService;

import java.util.List;

@RestController
@RequestMapping("/queue")
public class QueueController {

    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @PostMapping("/{id}/next")
    public ResponseEntity<TicketResponseDTO> callNext(@PathVariable(name = "id") Long id){
        try{
            return ResponseEntity.ok().body(queueService.callNext(id));
        }catch (NullPointerException e){
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/{id}/size")
    public ResponseEntity<Integer> getSizeQueue(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok().body(queueService.getSize(id));
    }

    @GetMapping
    public ResponseEntity<List<QueueResponseDTO>> getQueues(){
        return ResponseEntity.ok(queueService.getAllQueues());
    }



}
