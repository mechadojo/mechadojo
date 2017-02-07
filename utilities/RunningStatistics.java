package org.mechadojo.utilities;

/*
 *  Utility class to calculate running statistics on a value.
 *
 *  Calculates average, variance and standard deviation
 *
 *  Based on Donald Knuth's Art of Computer Programming Vol 2 pg 232 (3rd editition)
 *
 */

public class RunningStatistics {
    int count;

    double m1;
    double m2;

    public void clear() { count = 0; m1 = 0; m2= 0; }

    public void push(double x) {
        double delta, delta_n, delta_n2, term1;

        long n1 = count;
        count++;
        delta = x - m1;
        delta_n = delta / count;
        m1 += delta_n;
        m2 += delta * delta_n * n1;

        count++;
    }

    public double total() {
        return count;
    }

    public double mean() {
        return count > 0 ? m1: 0;
    }

    public double variance() {
        return count > 1 ? m2 / (count - 1.0) : 0;
    }

    public double standardDeviation() {
        return Math.sqrt( variance() );
    }

    public static RunningStatistics add(RunningStatistics a, RunningStatistics b) {
        RunningStatistics n = new RunningStatistics();
        n.count = a.count + b.count;
        double delta = b.m1 - a.m2;
        double delta2 = delta*delta;

        n.m1 = (a.count*a.m1 + b.count*b.m1) / n.count;
        n.m2 = a.m2 + b.m2 + delta2 * a.count * b.count / n.count;

        return n;
    }
}
