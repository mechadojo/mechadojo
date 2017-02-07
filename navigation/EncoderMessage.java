package org.mechadojo.navigation;

import org.mechadojo.stateflow.Message;

public class EncoderMessage extends Message {

    public int encoder;

    public long ticks;
    public long diff;
    public long period;

    public double delta;
    public double position;
    public double speed;
    public double acceleration;

    public EncoderMessage() {};
    public EncoderMessage(int encoder) {
        super();
        this.encoder = encoder;
    }

    public EncoderMessage(EncoderMessage msg) {
        super(msg);

        encoder = msg.encoder;
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


    @Override
    public String getLogHeader() {
        return "timestamp,id,diff,ticks,period,delta,position,speed,acceleration\r\n";
    }

    @Override
    public String getLogRow() {
        StringBuilder sb = new StringBuilder();
        sb.append(timestamp).append(',');
        sb.append(encoder).append(',');
        sb.append(ticks).append(',');
        sb.append(diff).append(',');
        sb.append(period).append(',');
        sb.append(delta).append(',');
        sb.append(position).append(',');
        sb.append(speed).append(',');
        sb.append(acceleration).append("\r\n");
        return sb.toString();
    }

}
