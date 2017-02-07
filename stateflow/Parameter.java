package org.mechadojo.stateflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Parameter {
    public Controller controller;
    public String path;
    public Message value;

    public void setValue(String source, Message msg)
    {
        Message previous = value;
        msg.previous = previous;
        value = msg;
        if (msg.hasChanged(previous)) {
            controller.updateParameter(path, msg);
        }
    }

    public Message getValue()
    {
        return value;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

}
