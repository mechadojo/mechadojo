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
        boolean changed = hasChanged(msg, previous );
        value = msg;
        controller.updateParameter(path, msg, previous, changed);
    }

    public boolean hasChanged(Message current, Message previous)
    {
        return true;
    }

    public Message getValue()
    {
        return value;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

}
