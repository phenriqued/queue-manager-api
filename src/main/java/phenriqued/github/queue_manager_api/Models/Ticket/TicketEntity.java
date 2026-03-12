package phenriqued.github.queue_manager_api.Models.Ticket;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import phenriqued.github.queue_manager_api.Models.Customer.CustomerEntity;

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

    @Column(nullable = false)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeTicket typeTicket;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


}
