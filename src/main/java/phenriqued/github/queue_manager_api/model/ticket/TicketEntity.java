package phenriqued.github.queue_manager_api.model.ticket;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import phenriqued.github.queue_manager_api.infra.exception.custom.IllegalDataException;
import phenriqued.github.queue_manager_api.infra.exception.custom.InvalidTicketOperationException;
import phenriqued.github.queue_manager_api.model.customer.CustomerEntity;
import phenriqued.github.queue_manager_api.model.queue.QueueEntity;

import java.time.LocalDateTime;
import java.util.Objects;

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

    @ManyToOne
    @JoinColumn(name = "queue_id", nullable = false)
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
        if(this.status == TicketStatus.PENDING || this.status == TicketStatus.IN_PROGRESS){
            this.owner = owner;
            this.typeTicket = owner.getIsPriority() ? TypeTicket.PRIORITY : TypeTicket.NORMAL;
            return;
        }
        throw new InvalidTicketOperationException("It is not possible to change the owner when the ticket's status is anything other than 'IN_PROGRESS' or 'PENDING'.");
    }
    public void startAttendance(){
        if(this.status != TicketStatus.PENDING) {
            throw new InvalidTicketOperationException("It is not possible to start a service request for a ticket that is not pending.");
        }
        this.status = TicketStatus.IN_PROGRESS;
    }
    public void statusCompleted() {
        if(this.status != TicketStatus.IN_PROGRESS){
            throw new InvalidTicketOperationException("It is not possible to complete a ticket that is not in progress.");
        }
        this.status = TicketStatus.COMPLETED;
    }
    public void statusCancel(){
        if(this.status != TicketStatus.PENDING) {
            throw new InvalidTicketOperationException("It is not possible to cancel a ticket that is completed or miss.");
        }
        this.status = TicketStatus.CANCELLED;
        this.queue = null;
    }
    public void statusMissed(){
        if(this.status != TicketStatus.IN_PROGRESS){
            throw new InvalidTicketOperationException("It is not possible to mark a ticket as miss once it has been completed.");
        }
        this.status = TicketStatus.MISSED;
    }
    public void statusPending(){
        if (this.status != TicketStatus.IN_PROGRESS){
            throw new InvalidTicketOperationException("It is not possible to mark a ticket as pending when it is not in progress.");
        }
        this.status = TicketStatus.PENDING;
    }

    public void setQueue(@NotNull QueueEntity queue){
        if(Objects.equals(this.queue.getId(), queue.getId())) throw new IllegalDataException("It is not possible to update the ticket queue to the queue itself.");
        this.queue = queue;
        statusPending();
    }
}
