package physics.model;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.lwjgl.opengl.GL11.*;
import static physics.LinearSolver.*;

public class CircularConstraint2D implements Constraint {

    private Particle2D p;
    private double[] center;
    private double radius;

    public CircularConstraint2D(Particle2D p, double[] center, double radius) {
        this.p = p;
        this.center = center;
        this.radius = radius;
    }

    @Override
    public Particle[] getParticles() {
        return new Particle[]{p};
    }

    @Override
    public double calculateC0() {
        double[] posDiff = vecDiff(p.getPosition(), center);
        return vecDot(posDiff, posDiff) - radius * radius;
    }

    @Override
    public double calculateC1() {
        double[] posDiff = vecDiff(p.getPosition(), center);
        return 2 * vecDot(posDiff, p.getVelocity());
    }

    @Override
    public double[][] calculateJ0() {
        double[] posDiff = vecDiff(p.getPosition(), center);
        return new double[][]{vecTimesScalar(posDiff, 2)};
    }

    @Override
    public double[][] calculateJ1() {
        return new double[][]{vecTimesScalar(p.getVelocity(), 2)};
    }

    @Override
    public void draw() {
        drawCircle(center, radius);
    }

    public static void drawCircle(double[] vect, double radius) {
        glBegin(GL_LINE_LOOP);
        glColor3d(0.0,1.0,0.0);
        for (int i = 0; i < 360; i += 18) {
            double degInRad = (i*Math.PI/180);
            glVertex2d(vect[0] + cos(degInRad)*radius,vect[1] + sin(degInRad)*radius);
        } glEnd();
    }
}
