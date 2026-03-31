package phenriqued.github.queue_manager_api.model.ticket;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import phenriqued.github.queue_manager_api.model.customer.CustomerEntity;
import phenriqued.github.queue_manager_api.model.queue.QueueEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_ticket")

@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class TicketEntity implements Comparable<TicketEntity>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "customer_id", nullable = true)
    private CustomerEntity owner;

    @Column(nullable = false)
    private String code;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeTicket typeTicket;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Setter @NotNull
    @ManyToOne
    @JoinColumn(name = "queue_id")
    private QueueEntity queue;

    public TicketEntity(CustomerEntity owner, String code, TypeTicket typeTicket, QueueEntity queue) {
        this.owner = owner;
        this.code = code;
        this.typeTicket = typeTicket;
        this.status = TicketStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.queue = queue;
    }

    public TicketEntity(String code, TypeTicket typeTicket, QueueEntity queue) {
        this.code = code;
        this.typeTicket = typeTicket;
        this.status = TicketStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.queue = queue;
    }


    @Override
    public int compareTo(TicketEntity other) {
        return this.createdAt.compareTo(other.createdAt);
    }

    public void changeOwner(@NotNull CustomerEntity owner){
        this.owner = owner;
        this.typeTicket = owner.getIsPriority() ? TypeTicket.PRIORITY : TypeTicket.NORMAL;
    }
    public void startAttendance(){
        this.status = TicketStatus.IN_PROGRESS;
    }

}
