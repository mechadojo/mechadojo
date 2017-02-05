package org.mechadojo.stateflow;

import java.util.HashMap;

public class Message {
    public long id;
    public String type;
    public long timestamp;

    public String serialize()
    {
        return String.format("%d : %s", id, type);
    }

    public Message clone() {
        Message m = new Message();
        copyTo(m);
        return m;
    }

    public int compare(Message m) {
        return 0;
    }

    public void copyTo(Message m) {
        m.id = id;
        m.type = type;
        m.timestamp = timestamp;
    }
}
