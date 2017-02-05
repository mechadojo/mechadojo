package org.mechadojo.stateflow;

import java.util.concurrent.ConcurrentHashMap;

public class ComponentLibrary extends StateFlowObject {
    ConcurrentHashMap<String, Component> components = new ConcurrentHashMap<>();

    public ComponentLibrary addComponents(Component... components) {
        for(Component c: components) {
            this.components.put(c.name, c);
        }
        return this;
    }

    public ComponentLibrary addComponent(String name, MessageHandler handler) {
        Component c = new Component();
        c.name = name;
        c.addOutput("OUT");
        c.addInput("IN", handler);
        components.put(c.name, c);
        return this;
    }

    public ComponentLibrary addComponent(String name, String outputs, String inputs, MessageHandler... handlers) {
        Component c = new Component();
        c.name = name;
        c.addOutputs(outputs);
        c.addInputs(inputs, handlers);
        components.put(c.name, c);
        return this;
    }

    public void setController(Controller controller) {
        super.setController(controller);
        for(Component c : components.values()) c.setController(controller);
    }

    public Component getComponent(String path) {
        int start = path.indexOf('.');
        if (start >= 0) {
            String n = path.substring(0, start);
            if (n != name) return null;
            String p = path.substring(start+1);
            return components.get(p);
        }

        return components.get(path);
    }
}
