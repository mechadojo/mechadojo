package org.mechadojo.stateflow;

import java.util.HashMap;

public class StateFlowObject {
    public Controller controller;
    public String name = "";
    public String path = null;
    public HashMap<String, String> properties = new HashMap<>();
    public String getPath()
    {
        return name;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
