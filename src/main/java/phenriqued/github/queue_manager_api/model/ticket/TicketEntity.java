package phenriqued.github.queue_manager_api.model.ticket;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import phenriqued.github.queue_manager_api.model.customer.CustomerEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_ticket")

@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "customer_id", nullable = true)
    private CustomerEntity owner;

    @Column(nullable = false)
    private String code;

    @Setter @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeTicket typeTicket;

    @Setter @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public TicketEntity(CustomerEntity owner, String code, TypeTicket typeTicket) {
        this.owner = owner;
        this.code = code;
        this.typeTicket = typeTicket;
        this.status = TicketStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public TicketEntity(String code, TypeTicket typeTicket) {
        this.code = code;
        this.typeTicket = typeTicket;
        this.status = TicketStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }


}
