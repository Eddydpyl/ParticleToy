package physics.model;

import static org.lwjgl.opengl.GL11.*;
import static physics.LinearSolver.*;

public class RodConstraint2D implements Constraint {

    private Particle2D p1;
    private Particle2D p2;
    private double distance;

    public RodConstraint2D(Particle2D p1, Particle2D p2, double distance) {
        this.p1 = p1;
        this.p2 = p2;
        this.distance = distance;
    }

    @Override
    public Particle[] getParticles() {
        return new Particle[]{p1, p2};
    }

    @Override
    public double calculateC0() {
        double[] posDiff = vecDiff(p1.getPosition(), p2.getPosition());
        return vecDot(posDiff, posDiff) - distance * distance;
    }

    @Override
   public double calculateC1() {
       double[] posDiff = vecDiff(p1.getPosition(), p2.getPosition());
       double[] velDiff = vecDiff(p1.getVelocity(), p2.getVelocity());
       return 2 * vecDot(posDiff, velDiff);
   }

    @Override
    public double[][] calculateJ0() {
        double[] posDiff = vecDiff(p1.getPosition(), p2.getPosition());
        return new double[][]{vecTimesScalar(posDiff, 2), vecNegate(vecTimesScalar(posDiff, 2))};
    }

    @Override
    public double[][] calculateJ1() {
        double[] velDiff = vecDiff(p1.getVelocity(), p2.getVelocity());
        return new double[][]{vecTimesScalar(velDiff, 2), vecNegate(vecTimesScalar(velDiff, 2))};
    }

    @Override
    public void draw() {
        glBegin(GL_LINES);
        glColor3d(0.8, 0.7, 0.6);
        glVertex2d(p1.getPosition()[0], p1.getPosition()[1]);
        glColor3d(0.8, 0.7, 0.6);
        glVertex2d(p2.getPosition()[0], p2.getPosition()[1]);
        glEnd();
    }
}
