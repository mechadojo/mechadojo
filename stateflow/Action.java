package org.mechadojo.stateflow;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Action extends StateFlowObject implements Network {
    public Behavior behavior;
    public HashMap<String, Connection> connections = new HashMap<>();
    public HashMap<String, String> settings = new HashMap<>();

    public String defaultInputPort = "";
    public String defaultOutputPort = "";

    public Component component;
    public String componentPath;


    public void run(HashMap<String, ArrayList<MessageRoute>> messages) {
      //  Log.d("StateFlow", "Run action: " + getPath());

        if (component == null && controller != null) {
            component = controller.getComponent(componentPath);
        }

        if (component != null) {
            component.run(messages, this);
        } else {
            Log.d("StateFlow", "Component not found: " + componentPath);
        }
    }


    public Behavior getBehavior() { return behavior; }

    public String getInputPath(String port) {
        return getInputPath(port, false);
    }

    public String getInputPath(String port, boolean includeComponent) {
        return String.format("%s {%s}({%s})", getPath(), includeComponent ? component.getPath() : "", port);
    }

    public String getOutputPath(String port) {
        return getOutputPath(port, false);
    }

    public String getOutputPath(String port, boolean includeComponent) {
        return String.format("{%s}({%s}) %s", getPath(), includeComponent ? component.getPath() : "", port);
    }

    /*
     * Network Interface
     */

    public void undefined(String port, MessageRoute msg) {
    }

    public void next(MessageRoute msg) {
        out(defaultOutputPort, msg);
    }

    public void idle(MessageRoute msg) {
        controller.addMessage(msg);
    }

    public void out(String port, MessageRoute msg) {
        Connection output = connections.get(port);

        boolean first = true;

        if (output != null) {
            msg.setSource(getOutputPath(port));

            for(MessagePath target : output.targets) {
                MessageRoute next = !first || target.isBehaviorPort
                                    ? new  MessageRoute(msg, target)
                                    : msg.setTarget(target);

                if (target.isBehaviorPort) {
                    // If we are sending the message to a behavior output port then pass
                    // the message back to the top action of the scope stack
                    if (next.scope != null) {
                        Action a = next.scope.action;
                        next.scope = next.scope.previous;
                        a.out(target.port, next);
                    } else {
                        undefined(target.port, next);
                    }
                } else {
                    controller.addMessage(next);
                    first = false;
                }
            }
        } else {
            undefined(port, msg);
        }
    }

    public Message getParameter(String path) {
        return controller.getParameter(path);
    }

    public void setParameter(String path, Message value) {
        controller.setParameter(path, getPath(), value);
    }

    public void postEvent(MessageRoute event) {

    }

    @Override
    public String getPath() {
        if (path == null) path = String.format("%s.%s", behavior.name, this.name);
        return path;
    }

    public void setController(Controller controller) {
        super.setController(controller);
        component = controller.getComponent(componentPath);
    }

    public void verbose(String msg) { controller.log("verbose", getPath(), msg); }
    public void debug(String msg)  { controller.log("debug", getPath(), msg); }
    public void info(String msg) { controller.log("info", getPath(), msg); }
    public void warning(String msg) { controller.log("warning", getPath(), msg); }
    public void error(String msg) { controller.log("error", getPath(), msg); }


}
