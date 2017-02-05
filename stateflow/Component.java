package org.mechadojo.stateflow;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class Component extends StateFlowObject {

    public HashMap<String, StateFlowObject> inputs = new HashMap<>();
    public HashMap<String, StateFlowObject> outputs = new HashMap<>();

    public HashMap<String, MessageHandler> ports = new HashMap<>();

    public boolean containsHandler(String port) {
        return ports.containsKey(port);
    }
    public Component() {}

    public Component(String name) {
        this.name = name;
    }

    public Component(String name, String outputs) {
        this.name = name;
        addOutputs(outputs);
    }

    public Component(String name, String outputs, String inputs, MessageHandler... handlers) {
        this.name = name;
        addOutputs(outputs);
        addInputs(inputs, handlers);
    }

    public Component addInput(String name) {
        if (!inputs.containsKey(name)) {
            StateFlowObject o = new StateFlowObject();
            o.name = name;
            inputs.put(name, o);
        }
        return this;
    }

    public Component addInput(String name, MessageHandler handler) {
        ports.put(name, handler);
        if (!inputs.containsKey(name)) {
            StateFlowObject o = new StateFlowObject();
            o.name = name;
            inputs.put(name, o);
        }
        return this;
    }

    public Component addOutput(String name) {
        StateFlowObject o = new StateFlowObject();
        o.name = name;
        outputs.put(name, o);
        return this;
    }

    public Component addOutputs(String outputs) {
        for(String outport : outputs.split(",")) {
            addOutput(outport);
        }
        return this;
    }

    public Component addInputs(String inputs, MessageHandler... handlers) {
        int index = 0;
        for(String inport : inputs.split(",")) {
            if (index < handlers.length) {
                addInput(inport, handlers[index] );
            } else {
                addInput(inport);
            }
            index++;
        }
        return this;
    }

    public void run(HashMap<String, ArrayList<MessageRoute>> messages, Action action) {

       // Log.d("StateFlow", "Run component: " + getPath() + " from action: " + action);


        for (String key : messages.keySet()) {
            MessageHandler handler = ports.get(key);

            for (MessageRoute msg : messages.get(key)) {
                if (handler != null) {
                    handler.handle(msg, action);
                } else {
                    action.undefined(msg.target.port, msg);
                }
            }
        }
    }

    public void setController(Controller controller) {
        super.setController(controller);
        for(StateFlowObject o : inputs.values()) o.setController(controller);
        for(StateFlowObject o : outputs.values()) o.setController(controller);
    }
}
