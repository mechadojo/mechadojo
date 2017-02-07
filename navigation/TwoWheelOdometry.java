package org.mechadojo.navigation;


import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.mechadojo.stateflow.Controller;
import org.mechadojo.utilities.RunningStatistics;

public class TwoWheelOdometry implements Runnable {
    public Controller controller;
    public DcMotor leftMotor;
    public DcMotor rightMotor;

    public EncoderMessage leftWheel = new EncoderMessage(1);
    public EncoderMessage rightWheel = new EncoderMessage(2);

    public PositionMessage position = new PositionMessage();

    public long leftEncoderZero = 0;
    public long rightEncoderZero = 0;

    // Convert encoder ticks into feet
    public double leftWheelFactor = 1.0;
    public double rightWheelFactor = 1.0;

    // Convert differential wheel travel to degrees
    public double turnFactor = 1.0;
    public double zeroHeading = 0.0;

    public long loopSleepPeriod = 20;
    public long loopPeriod;
    public long lastLoopStart;

    public RunningStatistics stats = new RunningStatistics();

    public boolean running = false;
    @Override
    public void run() {
        Log.d("Navigation", "TwoWheelOdometry thread starting.");
        running = true;
        lastLoopStart = controller.getTime();

        while(running) {
            long time = controller.getTime();
            if (!running) break;

            loopPeriod = time - lastLoopStart;
            lastLoopStart = time;
            stats.push(loopPeriod / 1000000.0);

            sampleEncoders();
            updatePosition();

            if (!running) break;
            try {
                Thread.sleep(loopSleepPeriod);
            } catch (Exception e) {}
        }

        Log.d("Navigation", "TwoWheelOdometry thread stopping.");
    }

    public void updatePosition() {
        double left = leftWheel.delta;
        double right = rightWheel.delta;

        // time period for position update is the average period for the
        // last individual wheel updates
        double dt = (leftWheel.period + rightWheel.period) / 2000000000.0;

        position.deltaDistance = (left + right) / 2.0;
        position.deltaAngle = (left - right) * turnFactor;

        double speed = position.deltaDistance / dt;
        position.linearAcceleration = (speed - position.linearSpeed) / dt;
        position.linearSpeed = speed;

        speed = position.deltaAngle / dt;
        position.angularAcceleration = (speed - position.angularSpeed) / dt;
        position.angularSpeed = speed;

        position.distance += position.deltaDistance;

        double theta = position.heading;
        position.heading += position.deltaAngle;

        // Calculate the position change based on the average angle
        // between the previous and current samples
        theta = ((theta + position.heading ) / 2.0) - zeroHeading;
        theta = Math.toRadians(theta);

        double dx = position.deltaDistance * Math.cos(theta);
        double dy = position.deltaDistance * Math.sin(theta);

        position.xPos += dx;
        position.yPos += dy;
    }

    public void updateEncoderMessage(EncoderMessage msg, long ticks, long time, double wheelFactor) {
        msg.diff = ticks - msg.ticks;
        msg.period = time - msg.timestamp;
        msg.timestamp = time;
        msg.ticks = ticks;

        msg.delta = msg.diff * wheelFactor;
        msg.position = msg.position + msg.delta;

        double dt = msg.period / 1000000000.0;
        double speed = msg.delta / dt;

        msg.acceleration = (speed - msg.speed) / dt;
        msg.speed = speed;
    }

    public void sampleEncoders() {
        long ticks = leftMotor.getCurrentPosition() - leftEncoderZero;
        long time = controller.getTime();

        updateEncoderMessage(leftWheel, ticks, time, leftWheelFactor);

        ticks = rightMotor.getCurrentPosition() - rightEncoderZero;
        time = controller.getTime();

        updateEncoderMessage(rightWheel, ticks, time, rightWheelFactor);

        controller.setParameter("odometry/left_wheel", "navigation", leftWheel.clone());
        controller.setParameter("odometry/right_wheel", "navigation", rightWheel.clone());
    }

    public void zeroEncoders() {
        leftEncoderZero = leftMotor.getCurrentPosition();
        rightEncoderZero = rightMotor.getCurrentPosition();
        leftWheel.position = 0.0;
        rightWheel.position = 0.0;
    }
}
