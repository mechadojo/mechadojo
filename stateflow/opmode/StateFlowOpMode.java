package org.mechadojo.stateflow.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.mechadojo.stateflow.Controller;
import org.mechadojo.stateflow.LogHandler;
import org.mechadojo.stateflow.messages.DoubleTriggerMessage;
import org.mechadojo.stateflow.messages.GamepadMessage;

import java.util.ArrayList;
import java.util.Collections;


public class StateFlowOpMode extends OpMode {

    public Controller controller;
    double startOpmodeTimer;
    public ArrayList<Double> timers = new ArrayList<>();

    long lastGamepad1Timestamp = 0;
    long lastGamepad2Timestamp = 0;

    public void init()
    {
        startOpmodeTimer = 0;
        controller = loadController();
        controller.resetTime();
        assignLog();
        controller.postEvent("opmode/init");

    }

    public void init_loop()
    {
        handleInitGamepad();
        controller.step();
        updateInitTelemetry();
        telemetry.update();
    }

    public void start()
    {
        startOpmodeTimer = controller.getSeconds();
        controller.stats.clear();
        controller.postEvent("opmode/start");
        Collections.sort(timers);
    }

    public void loop()
    {
        if (timers.size() > 0)
        {
            double next = timers.get(0);
            double time = controller.getSeconds() - startOpmodeTimer;

            if (time > next) {
                timers.remove(0);
                controller.postEvent("opmode/timer", new DoubleTriggerMessage(time, next));
            }
        }

        handleRunGamepad();
        controller.step();
        updateRunTelemetry();
        telemetry.update();
    }

    public void stop()
    {
        controller.postEvent("opmode/stop");
        controller.flushLogs();
    }

    public Controller loadController()
    {
        return new Controller();
    }

    public void assignLog() {
        controller.logHandler = new LogHandler() {
            public void log(String level, String source, String message) {
                if (level != "verbose")
                    telemetry.log().add("%s: %s", source, message);
            }
        };

        telemetry.log().setCapacity(6);
    }

    public void handleInitGamepad(){
        handleRunGamepad();
    }

    public void handleRunGamepad() {

        if(gamepad1 != null && gamepad1.user != Gamepad.ID_UNASSOCIATED && lastGamepad1Timestamp != gamepad1.timestamp)
        {
            controller.setParameter("driver/gamepad1", "opmode", new GamepadMessage(gamepad1));
            lastGamepad1Timestamp = gamepad1.timestamp;
        }

        if(gamepad2 != null && gamepad2.user != Gamepad.ID_UNASSOCIATED && lastGamepad2Timestamp != gamepad2.timestamp)
        {
            controller.setParameter("driver/gamepad2", "opmode", new GamepadMessage(gamepad2));
            lastGamepad2Timestamp = gamepad2.timestamp;
        }

        if (gamepad1 == null || gamepad1.user == Gamepad.ID_UNASSOCIATED)
        {
            if (controller.getParameter("driver/gamepad1") != null)
                controller.setParameter("driver/gamepad1", "opmode", null);
        }

        if (gamepad2 == null || gamepad2.user == Gamepad.ID_UNASSOCIATED)
        {
            if (controller.getParameter("driver/gamepad2") != null)
                controller.setParameter("driver/gamepad2", "opmode", null);
        }
    }

    public void updateInitTelemetry() {
        double avg = controller.stats.mean();
        double stddev = controller.stats.standardDeviation();

        telemetry.addData("Init Mode", String.format("%.2f (%.2f msec σ: %.4f)", controller.getSeconds(), avg, stddev));
    }

    public void updateRunTelemetry() {
        double avg = controller.stats.mean();
        double stddev= controller.stats.standardDeviation();

        telemetry.addData("Run Mode", String.format("%.2f (%.2f msec σ: %.4f)", controller.getSeconds() - startOpmodeTimer, avg, stddev));
    }
}
