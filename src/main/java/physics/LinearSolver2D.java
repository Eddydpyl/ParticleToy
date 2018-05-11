package physics;

import com.sun.javafx.geom.Vec2d;

public class LinearSolver2D {

    public static Vec2d vecAdd(Vec2d v1, Vec2d v2) {
        double x = v1.x + v2.x;
        double y = v1.y + v2.y;
        return new Vec2d(x,y);
    }

    public static Vec2d vecDiff(Vec2d v1, Vec2d v2) {
        double x = v1.x - v2.x;
        double y = v1.y - v2.y;
        return new Vec2d(x,y);
    }

    public static Vec2d vecTimesScalar(Vec2d v, double n) {
        double x = v.x * n;
        double y = v.y * n;
        return new Vec2d(x,y);
    }

    public static Vec2d vecTimesScalar(Vec2d v, int n) {
        double x = v.x * n;
        double y = v.y * n;
        return new Vec2d(x,y);
    }

    public static double vecDot(Vec2d v1, Vec2d v2) {
        double x = v1.x * v2.x;
        double y = v1.y * v2.y;
        return x + y;
    }

}
