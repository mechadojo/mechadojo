package org.mechadojo.stateflow.examples;

import org.mechadojo.stateflow.Action;
import org.mechadojo.stateflow.Controller;
import org.mechadojo.stateflow.MessageHandler;
import org.mechadojo.stateflow.MessageRoute;
import org.mechadojo.stateflow.messages.GamepadMessage;

public class GamepadHelloWorldController extends Controller {
    public GamepadHelloWorldController() {
        addLibrary("helloworld")
            // Add a component with 1 output, 1 input with handler
            .addComponent("hello/step_one", "OUT", "IN", new MessageHandler() {
                @Override
                public void handle(MessageRoute msg, Action action) {
                   action.next( msg );
                }
            })

            // Add a component with default output/input named OUT/IN and a single handler
            .addComponent("hello/step_two", new MessageHandler() {
                @Override
                public void handle(MessageRoute msg, Action action) {
                    if (msg.message instanceof GamepadMessage) {
                        GamepadMessage gp = (GamepadMessage) msg.message;

                        if (gp.a) {
                            action.info("A");
                        } else {
                            action.info("!A");
                        }
                    }

                }});

        addBehavior("helloworld")
                .addConnections("step_1(hello/step_one) OUT -> IN step_2(hello/step_two)")
                .addUpdateTrigger("driver/gamepad1", "IN step_1()" );

        initialize();
    }
}
