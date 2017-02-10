package org.mechadojo.navigation;


import org.mechadojo.stateflow.Controller;

public class PidController {
    public Controller controller;

    public double dt;
    public double value;
    public double target;

    public double error;
    public double errorRate;
    public double errorSum;

    public double maxError = 0.0;
    public double zeroPower = 0.0;
    public double kP = 0.0;
    public double kI = 0.0;
    public double kD = 0.0;

    public double output;
    public double last;
    public double result;


    public double maxOutput = 1.0;

    public double maxResult = 1.0;
    public double minResult = 0.0;

    public double maxSum = 1.0;

    public double update(double value, double dt, double last) {
        this.value = value;
        this.dt = dt;
        this.last = last;

        double err = target - value;

        errorSum += err * dt;
        if (errorSum > maxSum) errorSum = maxSum;
        if (errorSum < -maxSum) errorSum = -maxSum;

        errorRate = (err - error) / dt;
        error = err;

        if (error > maxError || error < -maxError) {
            if (error > maxError) output = maxOutput;
            else output = -maxOutput;
        } else {
            output = zeroPower + error * kP + errorSum * kI + errorRate * kD;
        }

        if (output > maxOutput) output = maxOutput;
        if (output < -maxOutput) output = -maxOutput;

        result = last + output;
        if (result > maxResult) return maxResult;
        if (result < -maxResult) return -maxResult;

        if (result > -minResult && result < minResult) {
            if (result < 0) result = -minResult;
            else result = minResult;
        }

        return result;
    }

    public void set(double target) {
        error = value - target;
        errorSum = 0;
        this.target = target;
    }
}
