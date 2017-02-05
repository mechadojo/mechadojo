package org.mechadojo.utilities;


public class RunningRegression {
    RunningStatistics xStats = new RunningStatistics();
    RunningStatistics yStats = new RunningStatistics();
    int count;
    double xyS;

    public void clear() {
        xStats.clear();
        yStats.clear();
        xyS = 0;
        count = 0;
    }

    public int total() {
        return count;
    }

    public void push(double x, double y) {
        xyS += (xStats.mean() - x) * (yStats.mean() - y) * (double)count / (double)(count+1);

        xStats.push(x);
        yStats.push(y);
        count++;
    }

    public double slope() {
        double xxS = xStats.variance() * (count - 1.0);
        return xyS / xxS;
    }

    public double intercept() {
        return yStats.mean() - slope() * xStats.mean();
    }

    public double correlation() {
        double t = xStats.standardDeviation() * yStats.standardDeviation();
        return xyS / ((count-1) * t);
    }

    static public RunningRegression add(RunningRegression a, RunningRegression b) {
        RunningRegression n = new RunningRegression();

        n.xStats = RunningStatistics.add(a.xStats, b.xStats);
        n.yStats = RunningStatistics.add(a.yStats, b.yStats);

        n.count = a.count + b.count;

        double delta_x = b.xStats.mean() - a.xStats.mean();
        double delta_y = b.yStats.mean() - a.yStats.mean();

        n.xyS = a.xyS + b.xyS +  (double)(a.count*b.count)*delta_x*delta_y / (double)n.count;

        return n;
    }
}
