package org.mechadojo.stateflow;


import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
    * Input Description : IN_PORT[$] [behavior.]action([component])
    * Output Description : [behavior.]action([component]) OUT_PORT[$]
    *
    * Connections are defined as output and intput pairs
    *     action() OUT -> IN action()
    *
    * Multiple inputs can be connected to a single output
    *     action() OUT -> IN action1() -> IN action2()
    *
    * or the same output can be defined multiple times
    *     action() OUT -> IN action1()
    *     action() OUT -> IN action2()
    *
    * The component only needs to be defined the first time an action
    * is used.
    *
    * Behavior name can usually be omitted based on context, however it
    * will be used at runtime to fully specify the action's name
    *
    * Input and Output ports can end with an '$' to indicate the
    * default port. Otherwise the first port specified is the default.
    *
    * Behavior ports can be routed using $()
    */

public class MessagePath {
    public String path = "";
    public String behavior = "";
    public String action = "";
    public String component = "";
    public String port = "";

    public boolean isDefaultPort = false;
    public boolean isBehaviorPort = false;
    public boolean isValidPath = false;

    static Pattern inputPattern = Pattern.compile("(\\w+)[$]?\\s+([a-zA-Z_0-9./]+)\\s*[(]\\s*([a-zA-Z_0-9./]*)\\s*[)]");
    static Pattern outputPattern = Pattern.compile("([a-zA-Z_0-9./]+)\\s*[(]\\s*([a-zA-Z_0-9./]*)\\s*[)]\\s*(\\w+)[$]?");

    public MessagePath() {};
    public MessagePath(String path) {
        this.path =  path;
    }

    public MessagePath(String behavior, String action, String component, String port) {
        this.behavior = behavior;
        this.action = action;
        this.component = component;
        this.port = port;
    }

    public void splitInput() {
        Matcher m = inputPattern.matcher(path);

        if (m.find()) {
            String behavior_action = m.group(2);

            int pos = behavior_action.indexOf('.');
            behavior = pos >= 0 ? behavior_action.substring(0, pos) : "";
            action =  pos >= 0 ? behavior_action.substring(pos+1) : behavior_action;

            component = m.group(3);
            port = m.group(1);

          //  Log.v("StateFlow", String.format("splitInput: '%s' : B:'%s' A:'%s' C:'%s' P:'%s'", path, behavior, action, component, port));

            updatePortStatus();

            isValidPath = true;
        } else {
            isValidPath = false;
        }
    }
    public void splitOutput() {
        Matcher m = outputPattern.matcher(path);

        if (m.find()) {
            String behavior_action = m.group(1);

            int pos = behavior_action.indexOf('.');
            behavior = pos >= 0 ? behavior_action.substring(0, pos) : "";
            action =  pos >= 0 ? behavior_action.substring(pos+1) : behavior_action;
            component = m.group(2);
            port = m.group(3);

           // Log.v("StateFlow", String.format("splitOutput: '%s' : B:'%s' A:'%s' C:'%s' P:'%s'", path, behavior, action, component, port));

            updatePortStatus();

            isValidPath = true;
        } else {
            isValidPath = false;
        }
    }
    private void updatePortStatus() {

        if (port.endsWith("$")) {
            isDefaultPort = true;
            port = port.substring(0, port.length()-1);
        }

        if (action.startsWith("$")) {
            isBehaviorPort = true;
            action = action.substring(1);
        }
    }

    static public MessagePath splitInput(String path) {
        MessagePath result = new MessagePath(path);
        result.splitInput();
        return result.isValidPath ? result : null;
    }

    static public MessagePath splitOutput(String path) {
        MessagePath result = new MessagePath(path);
        result.splitOutput();
        return result.isValidPath ? result : null;
    }

    public int compareTo(MessagePath rhs) {
        int result = behavior.compareTo(rhs.behavior);
        if (result != 0) return result;

        result = action.compareTo(rhs.action);
        if (result != 0) return result;

        return port.compareTo(rhs.port);
    }
}