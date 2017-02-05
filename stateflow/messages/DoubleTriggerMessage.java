package org.mechadojo.stateflow.messages;

import org.mechadojo.stateflow.Message;

public class DoubleTriggerMessage extends Message {
    public double value;
    public double limit;

    public DoubleTriggerMessage(double value, double limit)
    {
        this.value = value;
        this.limit = limit;

        type = "double_trigger";
    }

    public Message clone()
    {
        DoubleTriggerMessage m = new DoubleTriggerMessage(value, limit);
        copyTo(m);
        return m;
    }

    public String serialize() {
        String result = String.format("value: %.2f limit: %.2f", value, limit);
        return result;
    }
}
