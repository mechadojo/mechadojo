package org.mechadojo.stateflow.messages;

import org.mechadojo.stateflow.Message;

public class UpdateMessage extends Message {
    public Message current;
    public Message previous;
    public boolean changed;

    public UpdateMessage(Message current, Message previous, boolean changed) {
        this.current = current;
        this.previous = previous;
        this.changed = changed;
        this.type = "update";
    }

    public Message clone() {
        UpdateMessage m = new UpdateMessage(current, previous, changed);
        copyTo(m);
        return m;
    }

}
