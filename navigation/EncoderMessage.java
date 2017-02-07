package org.mechadojo.navigation;

import org.mechadojo.stateflow.Message;

public class EncoderMessage extends Message {

    public long ticks;
    public long diff;
    public long period;

    public double delta;
    public double position;
    public double speed;
    public double acceleration;

    public EncoderMessage() {};
    public EncoderMessage(EncoderMessage msg) {
        super(msg);

        diff = msg.diff;
        ticks = msg.ticks;
        period = msg.period;

        delta = msg.delta;
        position = msg.position;
        speed = msg.speed;
        acceleration = msg.acceleration;
    }

    @Override
    public Message clone() {
        return new EncoderMessage(this);
    }

}
