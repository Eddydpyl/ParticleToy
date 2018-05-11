package physics;

public class LinearSolver {

    public static double[] vecAdd(double[] v1, double[] v2) {
        if (v1.length != v2.length) throw new IllegalArgumentException();
        double[] res = new double[v1.length];
        for (int i = 0; i < v1.length; i++) {
            res[i] = v1[i] + v2[i];
        } return res;
    }

    public static double[] vecDiff(double[] v1, double[] v2) {
        if (v1.length != v2.length) throw new IllegalArgumentException();
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
        if (v1.length != v2.length) throw new IllegalArgumentException();
        double res = 0;
        for (int i = 0; i < v1.length; i++) {
            res += v1[i] * v2[i];
        } return res;
    }

    public static double vecModule(double[] v) {
        return Math.sqrt(vecDot(v, v));
    }

}
