package phenriqued.github.queue_manager_api.model.queue;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_queue")

@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class QueueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nameQueue;

    @OneToMany(mappedBy = "queue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketEntity> ticketQueue = new ArrayList<>();

    @Setter
    @Column(nullable = false)
    private int preferentialCalledCount;

    public QueueEntity(String nameQueue) {
        this.nameQueue = nameQueue;
        this.preferentialCalledCount = 0;
    }
}
