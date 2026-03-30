package phenriqued.github.queue_manager_api.controller.queue;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import phenriqued.github.queue_manager_api.dto.queue.QueueResponseDTO;
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
    public ResponseEntity<?> callNext(@PathVariable(name = "id") Long id){
        var ticket = queueService.callNext(id);
        if (ticket != null){
            return ResponseEntity.ok().body(ticket);
        }
        return ResponseEntity.ok().body("There are no tickets in the queue");
    }

    @GetMapping("/{id}/size")
    public ResponseEntity<String> getSizeQueue(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok().body("The queue has a total of "+ queueService.getSize(id) + " pending tickets!");
    }

    @GetMapping
    public ResponseEntity<List<QueueResponseDTO>> getQueues(){
        return ResponseEntity.ok(queueService.getAllQueues());
    }



}
