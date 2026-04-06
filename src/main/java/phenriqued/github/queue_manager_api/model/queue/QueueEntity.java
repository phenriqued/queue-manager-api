package phenriqued.github.queue_manager_api.model.queue;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public QueueEntity(String nameQueue) {
        this.nameQueue = nameQueue;
    }
}
