package com.paulfrmbrn.ssmdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

@Service
public class OrderFulfillmentProcess {

    private final Logger logger = LoggerFactory.getLogger(OrderFulfillmentProcess.class);

    private final StateMachineFactory<State, Event> stateMachineFactory;
    private final OrderRepository repository; // todo separate table?

    public OrderFulfillmentProcess(StateMachineFactory<State, Event> stateMachineFactory,
                                   OrderRepository repository) {
        this.stateMachineFactory = stateMachineFactory;
        this.repository = repository;
    }

    public static <S, E>  void initialize(StateMachine<S, E> stateMachine, S state)  {
        stateMachine.getStateMachineAccessor().doWithAllRegions(access -> access
                .resetStateMachine(new DefaultStateMachineContext<>(state, null, null,null)));
    }

    public Order confirmOrder(Long id, String comment)  {

        var order = repository.findById(id).orElseThrow(() -> new IllegalStateException("Order does not exist"));
        logger.info("order found: {}", order);

        var stateMachine = stateMachineFactory.getStateMachine("OrderFulfillmentProcessStateMachine");
        initialize(stateMachine, State.valueOf(order.getStatus()));

        stateMachine.getExtendedState().getVariables().put("orderId", id);
        stateMachine.getExtendedState().getVariables().put("comment", comment);

        stateMachine.start();
        logger.info("SM: hashCode={}", stateMachine.hashCode());
        try {
            Thread.sleep(1000L); // for the sake of tests
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        var eventSentResult = stateMachine.sendEvent(Event.CONFIRM);
        if (!eventSentResult) {
            logger.warn("event was not sent");
            throw new IllegalStateException("Order is in wrong state");
        }
        stateMachine.stop();


        var updatedOrder = (Order) stateMachine.getExtendedState().getVariables().get("updatedOrder");
        logger.info("updated order: {}", updatedOrder);
        return updatedOrder;
    }


}
