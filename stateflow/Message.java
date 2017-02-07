package org.mechadojo.stateflow;

import java.util.HashMap;

public class Message {
    public long id;
    public String type;
    public long timestamp;
    public Message previous;

    public String serialize()
    {
        return String.format("{ id = %d, type = \"%s\", timestamp = %d }", id, type, timestamp);
    }

    public Message() {}
    public Message(Message msg) {
        type = msg.type;
        timestamp = msg.timestamp;
    }
    public Message clone() {
        Message m = new Message(this);
        return m;
    }

    public int compare(Message msg) {
        return 0;
    }

    public boolean hasChanged(Message msg) { return true; }
}
