package org.mechadojo.stateflow.messages;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.mechadojo.stateflow.Message;

public class GamepadMessage extends Message {
    public float left_stick_x = 0f;
    public float left_stick_y = 0f;
    public float right_stick_x = 0f;
    public float right_stick_y = 0f;
    public boolean dpad_up = false;
    public boolean dpad_down = false;
    public boolean dpad_left = false;
    public boolean dpad_right = false;
    public boolean a = false;
    public boolean b = false;
    public boolean x = false;
    public boolean y = false;
    public boolean guide = false;
    public boolean start = false;
    public boolean back = false;
    public boolean left_bumper = false;
    public boolean right_bumper = false;
    public boolean left_stick_button = false;
    public boolean right_stick_button = false;
    public float left_trigger = 0f;
    public float right_trigger = 0f;

    public GamepadMessage(Gamepad gamepad)
    {
        type = "gamepad";
        if (gamepad != null)
        {
            left_stick_x = gamepad.left_stick_x;
            left_stick_y = gamepad.left_stick_y;
            right_stick_x = gamepad.right_stick_x;
            right_stick_y = gamepad.right_stick_y;
            dpad_up = gamepad.dpad_up;
            dpad_down = gamepad.dpad_down;
            dpad_left = gamepad.dpad_left;
            dpad_right = gamepad.dpad_right;
            a = gamepad.a;
            b = gamepad.b;
            x = gamepad.x;
            y = gamepad.y;
            guide = gamepad.guide;
            back = gamepad.back;
            start = gamepad.start;
            left_bumper = gamepad.left_bumper;
            right_bumper = gamepad.right_bumper;
            left_stick_button = gamepad.left_stick_button;
            right_stick_button = gamepad.right_stick_button;
            left_trigger = gamepad.left_trigger;
            right_trigger = gamepad.right_trigger;
        }
    }

    public GamepadMessage()
    {
        type = "gamepad";
    }

    public Message clone()
    {
        GamepadMessage m = new GamepadMessage();
        m.timestamp = timestamp;
        m.left_stick_x = left_stick_x;
        m.left_stick_y = left_stick_y;
        m.right_stick_x = right_stick_x;
        m.right_stick_y = right_stick_y;
        m.dpad_up = dpad_up;
        m.dpad_down = dpad_down;
        m.dpad_left = dpad_left;
        m.dpad_right = dpad_right;
        m.a = a;
        m.b = b;
        m.x = x;
        m.y = y;
        m.guide = guide;
        m.back = back;
        m.start = start;
        m.left_bumper = left_bumper;
        m.right_bumper = right_bumper;
        m.left_stick_button = left_stick_button;
        m.right_stick_button = right_stick_button;
        m.left_trigger = left_trigger;
        m.right_trigger = right_trigger;

        return m;
    }
}
