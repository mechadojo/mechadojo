package org.mechadojo.navigation;

import org.mechadojo.stateflow.Message;

public class PositionMessage extends Message {
    public double distance;
    public double heading;

    public double deltaAngle;
    public double deltaDistance;

    public double linearAcceleration;
    public double angularAcceleration;
    public double linearSpeed;
    public double angularSpeed;
    public double xPos;
    public double yPos;

    public PositionMessage() {super();}
    public PositionMessage(PositionMessage msg) {
        super(msg);
        msg.distance = distance;
        msg.heading = heading;
        msg.deltaAngle = deltaAngle;
        msg.deltaDistance = deltaDistance;
        msg.linearSpeed = linearSpeed;
        msg.angularSpeed = angularSpeed;
        msg.xPos = xPos;
        msg.yPos = yPos;
    }
}
