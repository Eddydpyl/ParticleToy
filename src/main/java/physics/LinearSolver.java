package physics;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class LinearSolver {

    public static double[] vecAdd(double[] v1, double[] v2) {
        if (v1.length != v2.length) throw new IllegalArgumentException("Vectors have different lengths.");
        double[] res = new double[v1.length];
        for (int i = 0; i < v1.length; i++) {
            res[i] = v1[i] + v2[i];
        } return res;
    }

    public static double[] vecDiff(double[] v1, double[] v2) {
        if (v1.length != v2.length) throw new IllegalArgumentException("Vectors have different lengths.");
        double[] res = new double[v1.length];
        for (int i = 0; i < v1.length; i++) {
            res[i] = v1[i] - v2[i];
        } return res;
    }

    public static double[] vecTimesScalar(double[] v, double n) {
        double[] res = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            res[i] = v[i] * n;
        } return res;
    }

    public static double[] vecTimesScalar(double[] v, int n) {
        double[] res = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            res[i] = v[i] * n;
        } return res;
    }

    public static double[] vecNegate(double[] v) {
        double[] res = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            res[i] = - v[i];
        } return res;
    }

    public static double vecDot(double[] v1, double[] v2) {
        if (v1.length != v2.length) throw new IllegalArgumentException("Vectors have different lengths.");
        double res = 0;
        for (int i = 0; i < v1.length; i++) {
            res += v1[i] * v2[i];
        } return res;
    }

    public static double vecModule(double[] v) {
        return Math.sqrt(vecDot(v, v));
    }

    public static double[] vecNorm(double[] v) {
        return vecTimesScalar(v, 1 / vecModule(v));
    }

    public static double[] vecCross(double[] v1, double[] v2) {
        if (v1.length != v2.length) throw new IllegalArgumentException("Vectors have different lengths.");
        if (v1.length == 3) {
            return new double[]{
                    v1[1] * v2[2] - v2[1] * v1[2],
                    v2[0] * v1[2] - v1[0] * v2[2],
                    v1[0] * v2[1] - v2[0] * v1[1]
            };
        } else if (v1.length == 2)
            return new double[]{v1[0] * v2[1] - v1[1] * v2[0]};
        else throw new IllegalArgumentException("Unsupported number of dimensions.");
    }

    public static double[] vecCross(double[] v, double n) {
        if (v.length != 2) throw new IllegalArgumentException("Unsupported number of dimensions.");
        return new double[]{v[1] * n, v[0] * (-n)};
    }

    public static double[] vecCross(double n, double[] v) {
        if (v.length != 2) throw new IllegalArgumentException("Unsupported number of dimensions.");
        return new double[]{v[1] * (-n), v[0] * n};
    }

    public static double[] vecInv(double[] v) {
        double[] res = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            res[i] = 1 / v[i];
        } return res;
    }

    /**
     * @param p1 Some point in space.
     * @param p2 Some other point in space.
     * @return Distance between two points.
     */
    public static double vecDistance(double[] p1, double[] p2) {
        throw new NotImplementedException();
    }

    /**
     * @param v1 Origin point of the line.
     * @param v2 End point of the line.
     * @param p Some other point in space.
     * @return Distance between a line (v1 -> v2) and a point (p).
     */
    public static double vecDistance(double[] v1, double[] v2, double[] p) {
        return Math.abs((v2[0] - v1[0]) * (v1[1] - p[1]) - (v1[0] - p[0])  *(v2[1] - v1[1])) /
                Math.sqrt(Math.pow(v2[0] - v1[0], 2) + Math.pow(v2[1] - v1[1], 2));
    }

    /**
     * @param v1 Origin point of first line.
     * @param v2 End point of first line.
     * @param u1 Origin point of second line.
     * @param u2 End point of second line.
     * @return Distance between line (v1 -> v2) and line (u1 -> u2).
     */
    public static double vecDistance(double[] v1, double[] v2, double[] u1, double[] u2) {
        throw new NotImplementedException();
    }

    /**
     * @param p1 Origin point of first line.
     * @param v1 Direction of first line.
     * @param p2 Origin point of second line.
     * @param v2 Direction of second line.
     * @return Intersection of both lines, if it exists, else average location of the lines.
     */
    public static double[] vecIntersection(double p1[], double[] v1, double[] p2, double[] v2) {
        if (v1.length != v2.length || p1.length != p2.length || p1.length != v1.length)
            throw new IllegalArgumentException("Vectors have different lengths.");
        if (v1.length != 2 && v1.length != 3)
            throw new IllegalArgumentException("Unsupported number of dimensions.");
        if (p1.length == 2) {
            double x1 = p1[0] + v1[0];
            double y1 = p1[1] + v1[1];
            double x2 = p2[0] + v2[0];
            double y2 = p2[1] + v2[1];

            double d = ((p1[0] - x1) * (p2[1] - y2)) - ((p1[1] - y1) * (p2[0] - x2));
            if(d == 0) // If parallel, defaults to the average location of the lines.
                return new double[]{(p1[0] + p2[0]) * 0.5, (p1[1] + p2[1]) * 0.5f};
            else {
                double a = (p1[0] * y1) - (p1[1] * x1);
                double b = (p2[0] * y2) - (p2[1] * x2);
                return new double[]{((a * (p2[0] - x2)) - ((p1[0] - x1) * b)) / d, ((a * (p2[1] - y2)) - ((p1[1] - y1) * b)) / d};
            }
        } else {
            // Three dimensions not yet implemented.
            throw new NotImplementedException();
        }
    }
}
