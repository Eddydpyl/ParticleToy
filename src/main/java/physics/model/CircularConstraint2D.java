package physics.model;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.lwjgl.opengl.GL11.*;
import static physics.LinearSolver.*;

public class CircularConstraint2D implements Constraint {

    private static final double SMALL_VALUE = 0.0000000001;

    private Particle2D particle;
    private double[] center;
    private double radius;

    public CircularConstraint2D(Particle2D particle, double[] center, double radius) {
        checkDistance(particle, center);
        this.particle = particle;
        this.center = checkCenter(center);
        this.radius = radius;
    }

    private void checkDistance(Particle particle, double[] center) {
        if (vecDiff(particle.getPosition(), center) == new double[]{0.0, 0.0})
            throw new IllegalArgumentException("The particle and center can not be at the same coordinates.");
    }

    private double[] checkCenter(double[] center) {
        for (int i = 0; i < center.length; i++) {
            if (center[i] == 0) center[i] = SMALL_VALUE;
        } return center;
    }

    @Override
    public Particle[] getParticles() {
        return new Particle[]{particle};
    }

    @Override
    public double calculateC0() {
        double[] posDiff = vecDiff(particle.getPosition(), center);
        return vecDot(posDiff, posDiff) - radius * radius;
    }

    @Override
    public double calculateC1() {
        double[] posDiff = vecDiff(particle.getPosition(), center);
        return 2 * vecDot(posDiff, particle.getVelocity());
    }

    @Override
    public double[][] calculateJ0() {
        double[] posDiff = vecDiff(particle.getPosition(), center);
        return new double[][]{vecTimesScalar(posDiff, 2)};
    }

    @Override
    public double[][] calculateJ1() {
        return new double[][]{vecTimesScalar(particle.getVelocity(), 2)};
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
