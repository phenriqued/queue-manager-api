CREATE TABLE tb_ticket(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    type_ticket VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    customer_id BIGINT,
    queue_id BIGINT NOT NULL,

    CONSTRAINT fk_ticket_customer FOREIGN KEY (customer_id) REFERENCES tb_customer(id),
    CONSTRAINT fk_ticket_queue FOREIGN KEY (queue_id) REFERENCES tb_queue(id)
);