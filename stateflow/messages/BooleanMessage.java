package org.mechadojo.stateflow.messages;

import org.mechadojo.stateflow.Message;

public class BooleanMessage extends Message {
    public boolean value;

    public BooleanMessage(boolean value)
    {
        this.value = value;

        type = "boolean";
    }

    public Message clone()
    {
        return new BooleanMessage(value);
    }

    public String serialize() {
        String result = String.format("value: %s ", value ? "True" : "False");
        return result;
    }
}
