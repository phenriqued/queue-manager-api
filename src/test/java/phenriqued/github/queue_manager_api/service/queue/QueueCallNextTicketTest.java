package phenriqued.github.queue_manager_api.service.queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import phenriqued.github.queue_manager_api.infra.exception.custom.NoTicketInQueueException;
import phenriqued.github.queue_manager_api.model.queue.QueueEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketEntity;
import phenriqued.github.queue_manager_api.model.ticket.TicketStatus;
import phenriqued.github.queue_manager_api.model.ticket.TypeTicket;
import phenriqued.github.queue_manager_api.repository.queue.QueueRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class QueueCallNextTicketTest {

    @Autowired
    private QueueService queueService;
    @Autowired
    private QueueRepository queueRepository;

    @BeforeEach
    @Rollback
    void setup(){
        QueueEntity queue = queueRepository.findById(1L).orElseThrow();
        TicketEntity ticket1 = new TicketEntity("P-10001", TypeTicket.PRIORITY, queue);
        TicketEntity ticket2 = new TicketEntity("N-10001", TypeTicket.NORMAL, queue);
        TicketEntity ticket3 = new TicketEntity("P-10002", TypeTicket.PRIORITY, queue);
        TicketEntity ticket4 = new TicketEntity("N-10002", TypeTicket.NORMAL, queue);

        queueService.addToQueue(ticket1);
        queueService.addToQueue(ticket2);
        queueService.addToQueue(ticket3);
        queueService.addToQueue(ticket4);
    }

    @Test
    @DisplayName("The next ticket should be called following the business rule P-P-N. If it doesn't have priority in the queue, a normal ticket should be called.")
    void callNextCaseOne() {
        Long idQueue = 1L;
        var firstTicketPriority = queueService.callNext(idQueue);
        var secondTicketPriority = queueService.callNext(idQueue);
        var firstTicketNormal = queueService.callNext(idQueue);
        var thirdTicketPriority = queueService.callNext(idQueue);

        assertEquals(TypeTicket.PRIORITY, firstTicketPriority.typeTicket());
        assertEquals(TicketStatus.IN_PROGRESS, firstTicketPriority.status());

        assertEquals(TypeTicket.PRIORITY, secondTicketPriority.typeTicket());
        assertEquals(TicketStatus.IN_PROGRESS, secondTicketPriority.status());

        assertEquals(TypeTicket.NORMAL, firstTicketNormal.typeTicket());
        assertEquals(TicketStatus.IN_PROGRESS, firstTicketNormal.status());

        assertEquals(TypeTicket.NORMAL, thirdTicketPriority.typeTicket());
        assertEquals(TicketStatus.IN_PROGRESS, thirdTicketPriority.status());
    }

    @Test
    @DisplayName("When calling the next option and there are no more tickets, it should throw an exception.")
    void callNextCaseSecond() {
        Long idQueue = 1L;
        queueService.callNext(idQueue);
        queueService.callNext(idQueue);
        queueService.callNext(idQueue);
        queueService.callNext(idQueue);

        assertThrows(NoTicketInQueueException.class, () -> queueService.callNext(idQueue));
    }

    @Test
    @DisplayName("should deal with the next ticket in an atomic way and unique tickets")
    void shouldHandleAtomicNextTicket() throws Exception {
        Long idQueue = 1L;
        int numberOfThreads = 10;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(1);

        Set<Long> deliveredTicketIds = Collections.synchronizedSet(new HashSet<>());
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                try {
                    latch.await();
                    var ticket = queueService.callNext(idQueue);
                    if (Objects.nonNull(ticket)) {
                        deliveredTicketIds.add(ticket.id());
                    }
                } catch (NoTicketInQueueException e) {
                    failCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        latch.countDown();
        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);

        assertEquals(4, deliveredTicketIds.size());
        assertEquals(6, failCount.get());
    }


}
