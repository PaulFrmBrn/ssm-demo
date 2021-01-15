package com.paulfrmbrn.ssmdemo;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfiguration extends EnumStateMachineConfigurerAdapter<State, Event> {

    private final ConfirmOrderAction confirmOrderAction;

    public StateMachineConfiguration(ConfirmOrderAction confirmOrderAction) {
        this.confirmOrderAction = confirmOrderAction;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<State, Event> config) throws Exception {
        config
                .withConfiguration()
                .taskExecutor(new SyncTaskExecutor()) // Juergen Hoelle: Mainly intended for testing scenarios.
                .autoStartup(false);
    }

    @Override
    public void configure(StateMachineStateConfigurer<State, Event> states) throws Exception {
        states.withStates()
                .initial(State.CREATED)
                .state(State.CONFIRMED)
                .end(State.FULFILLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
        transitions
                .withExternal()
                .source(State.CREATED).target(State.CONFIRMED).event(Event.CONFIRM).action(confirmOrderAction)
                .and().withExternal()
                .source(State.CONFIRMED).target(State.FULFILLED).event(Event.FULFILL).action(confirmOrderAction); // todo fix
    }


}
