package org.mechadojo.stateflow.examples;

import org.mechadojo.stateflow.Action;
import org.mechadojo.stateflow.Controller;
import org.mechadojo.stateflow.MessageHandler;
import org.mechadojo.stateflow.MessageRoute;

public class DelayedHelloWorldController extends Controller {
    public DelayedHelloWorldController() {
        addLibrary("helloworld")
            // Add a component with 1 output, 1 input with handler
            .addComponent("hello/step_one", "OUT", "IN", new MessageHandler() {
                @Override
                public void handle(MessageRoute msg, Action action) {
                    action.info("Hello");
                    action.next(msg.delay(2.0));
                }
            })

            // Add a component with default output/input named OUT/IN and a single handler
            .addComponent("hello/step_two", new MessageHandler() {
                @Override
                public void handle(MessageRoute msg, Action action) {
                    action.info("World!");
                }});

        addBehavior("helloworld")
                .addConnections("step_1(hello/step_one) OUT -> IN step_2(hello/step_two)")
                .addEventTrigger("opmode/start", "IN step_1()" );

        initialize();
    }
}
