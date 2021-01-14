package com.paulfrmbrn.ssmdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    Iterable<Order> getAll() {
        return repository.findAll();
    }

    Order get(Long id) {
        return repository.findById(id).orElse(null);
    }

    Order create(String comment) {
        return repository.save(new Order(null, State.CREATED.name(), comment));
    }

    Order update(Order order) {
        return repository.save(order);
    }

    Order confirm(Long id, String comment) {
        logger.info("confirm(): id={}, comment={}", id, comment);
        return repository.findById(id).map(it -> {
            if (!it.getStatus().equals(State.CREATED.name())) {
                throw new IllegalStateException(String.format("Order is in wrong state: expected=%s, got=%s", State.CREATED.name(), it.getStatus()));
            }
            logger.info("saving confirmation: id={}", id);
            return repository.save(new Order(it.getId(), State.CONFIRMED.name(), comment));
        }).orElseThrow(() -> new IllegalStateException("Order does not exist"));
    }

    Order fulfill(Long id, String comment) {
        logger.info("fulfill(): id={}, comment={}", id, comment);
        return repository.findById(id).map(it -> {
            logger.info("saving fulfillment: id={}", id);
            return repository.save(new Order(it.getId(), State.FULFILLED.name(), comment));
        }).orElseThrow(() -> new IllegalStateException("Order does not exist"));
    }

}
