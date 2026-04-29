package phenriqued.github.queue_manager_api.dto.queue;

public record QueueMetricsDTO(
        int totalTickets,
        int totalPending,
        int totalTicketsCompleted,
        int totalTicketsCancel,
        int totalTicketsMissed,
        double averageWaitingTime,
        double averageServiceTime) {
}
