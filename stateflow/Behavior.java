package org.mechadojo.stateflow;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class Behavior extends Component {

    HashMap<String, Action> actions = new HashMap<>();
    HashMap<String, ActionGroup> groups = new HashMap<>();
    HashMap<String, Connection> connections = new HashMap<>();

    Action getAction(String path) {
        return actions.get(path);
    }

    public void run(HashMap<String, ArrayList<MessageRoute>> messages, Action action) {
        for(String port : messages.keySet()) {
            Connection output = connections.get(port);

            for(MessageRoute msg : messages.get(port)) {
                if (output == null) {
                    action.undefined(port, msg);
                    continue;
                }

                boolean first = true;

                for (MessagePath target : output.targets) {
                    MessageRoute next = !first
                            ? new MessageRoute(msg, target)
                            : msg.setTarget(target);

                    next.scope = controller.getNextScope(next.scope, action);
                    controller.addMessage(next);
                    first = false;
                }
            }
        }
    }


    public Behavior addUpdateTrigger(String trigger, String path) {
        MessagePath inport = MessagePath.splitInput(path);
        if (inport == null) return this;
        inport.behavior = name;
        addInputAction(inport);

        MessageTrigger mt = new MessageTrigger();
        mt.pattern = Pattern.compile(trigger);
        mt.target = inport;
        mt.type = "data/update";

        String key = String.format("%s : %s : %s", name, trigger, path);
        controller.updateTriggers.put(key, mt);
        return this;
    }

    public Behavior addEventTrigger(String event, String path) {
        MessagePath inport = MessagePath.splitInput(path);
        if (inport == null) return this;
        inport.behavior = name;
        addInputAction(inport);

        MessageTrigger mt = new MessageTrigger();
        mt.pattern = Pattern.compile(event);
        mt.target = inport;
        mt.type = "data/event";

        String key = String.format("%s : %s : %s", name, event, path);
        controller.eventTriggers.put(key, mt);
        return this;
    }

    public Behavior addConnection(String path) {


        String[] parts = path.split("->");

        Log.d("StateFlow", "Parse Output:" + parts[0]);
        MessagePath outport = MessagePath.splitOutput(parts[0]);

        if (outport == null)
            return this;

        outport.behavior = name;

        Connection conn = outport.isBehaviorPort
                                ? addBehaviorInput(outport.port)
                                : addOutputAction(outport);


        for(int i=1;i<parts.length;i++) {
            Log.d("StateFlow", "Parse Input:" + parts[i]);

            MessagePath inport = MessagePath.splitInput(parts[i]);
            if (inport == null) continue;
            inport.behavior = name;

            // There must be at least one action in the connection definition
            if (outport.isBehaviorPort && inport.isBehaviorPort)
                continue;

            conn.targets.add(inport);

            if (inport.isBehaviorPort) {
                addBehaviorOutput(inport.port);
            } else {
                addInputAction(inport);
            }
        }

        return this;
    }

    public Connection addBehaviorInput(String port) {
        if (!inputs.containsKey(port)) {
            StateFlowObject o = new StateFlowObject();
            o.name = port;
            inputs.put(port, o);
        }

        Connection conn = connections.get(port);
        if (conn == null) {
            conn = new Connection();
            connections.put(port, conn);
        }
        return conn;
    }


    public Connection addOutputAction(MessagePath outport) {
        Action out = actions.get(outport.action);
        if (out == null) {
            out = new Action();
            out.behavior = this;
            out.name = outport.action;
            Log.d("StateFlow", "created action: " + out.name);
            actions.put(out.name, out);


        }

        if (!outport.component.isEmpty()) {
            //Log.d("StateFlow", "addOutputAction: " + out.name + " = " + outport.component);
            out.componentPath = outport.component;
        }

        if (outport.isDefaultPort) {
            out.defaultOutputPort = outport.port;
        } else if (out.defaultOutputPort == null || out.defaultOutputPort.isEmpty()) {
            out.defaultOutputPort = outport.port;
        }

        Connection conn = out.connections.get(outport.port);
        if (conn == null) {
            conn = new Connection();
            out.connections.put(outport.port, conn);
        }
        return conn;
    }

    public void addBehaviorOutput(String port) {
        if (!outputs.containsKey(port)) {
            StateFlowObject o = new StateFlowObject();
            o.name = port;
            outputs.put(port, o);
        }
    }

    public void addInputAction(MessagePath inport) {
        Action in = actions.get(inport.action);
        if (in == null) {
            in = new Action();
            in.name = inport.action;
            Log.d("StateFlow", "created action: " + in.name);
            in.behavior = this;
            actions.put(in.name, in);
        }

        if (!inport.component.isEmpty())
        {
         //   Log.d("StateFlow", "addInputAction: " + in.name + " = " + inport.component);
            in.componentPath = inport.component;
        }

        if (inport.isDefaultPort) {
            in.defaultInputPort = inport.port;
        } else if (in.defaultInputPort == null || in.defaultInputPort.isEmpty()) {
            in.defaultInputPort = inport.port;
        }
    }

    public Behavior addConnections(String... paths) {
        for(String path: paths) {
            addConnection(path);
        }
        return this;
    }

    public void setController(Controller controller) {
        super.setController(controller);
        for(Action a: actions.values()) {
            a.behavior = this;
            a.setController(controller);
        }
    }
}
