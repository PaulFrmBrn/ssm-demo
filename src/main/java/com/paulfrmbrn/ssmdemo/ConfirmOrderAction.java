package com.paulfrmbrn.ssmdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Service;

@Service
public class ConfirmOrderAction implements Action<State, Event> {

    private final Logger logger = LoggerFactory.getLogger(ConfirmOrderAction.class);

    private final OrderRepository repository;

    public ConfirmOrderAction(OrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(StateContext<State, Event> context) {

        var orderId = (Long) context.getExtendedState().getVariables().get("orderId");
        var comment = (String) context.getExtendedState().getVariables().get("comment");

        logger.info("ConfirmOrderAction.execute(): orderId={}, comment={}", orderId, comment);
        var updatedOrder = repository.save(new Order(orderId, State.CONFIRMED.name(), comment));

        context.getExtendedState().getVariables().put("updatedOrder", updatedOrder);

    }
}
