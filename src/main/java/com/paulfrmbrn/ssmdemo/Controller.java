package com.paulfrmbrn.ssmdemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class Controller {

    private final OrderService orderService;
    private final OrderFulfillmentProcess process;

    public Controller(OrderService orderService, OrderFulfillmentProcess process) {
        this.orderService = orderService;
        this.process = process;
    }

    @GetMapping("/orders")
    Iterable<Order> getAll() {
        return orderService.getAll();
    }

    @GetMapping("/orders/{id}")
    Order get(@PathVariable Long id) {
        return orderService.get(id);
    }

    @PostMapping("/orders")
    Order create(@RequestBody CommentData data) {
        return orderService.create(data.getComment());
    }

    @PutMapping("/orders/{id}")
    Order update(@PathVariable Long id, @RequestBody Order order) {
        return orderService.update(new Order(id, order.getStatus(), order.getComment()));
    }

    @PostMapping("/orders/{id}/confirm")
    Order confirm(@PathVariable Long id, @RequestBody CommentData data) {
        //return orderService.confirm(id, data.getComment());
        return process.confirmOrder(id, data.getComment());
    }

    @PostMapping("/orders/{id}/fulfill")
    Order fulfill(@PathVariable Long id, @RequestBody CommentData data) {
        return orderService.fulfill(id, data.getComment());
    }
}
