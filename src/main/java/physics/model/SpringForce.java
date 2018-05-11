package physics.model;

import com.sun.javafx.geom.Vec2d;

import static org.lwjgl.opengl.GL11.*;
import static physics.LinearSolver2D.*;

public class SpringForce implements Force {

    private Particle p1, p2;
    private double ks, kd;
    private double rest;

    public SpringForce(Particle p1, Particle p2, double ks, double kd, double rest) {
        this.p1 = p1;
        this.p2 = p2;
        this.ks = ks;
        this.kd = kd;
        this.rest = rest;
    }

    @Override
    public void apply() {
        // Hook's Spring Law
        Vec2d posDiff = vecDiff(p1.getPosition(), p2.getPosition());
        Vec2d velDiff = vecDiff(p1.getVelocity(), p2.getVelocity());
        double module = vecModule(posDiff);
        double magnitude = (ks * (module - rest) + kd * (vecDot(posDiff, velDiff) / module)) / module;
        Vec2d spring = vecTimesScalar(posDiff, magnitude);
        p1.setForces(vecDiff(p1.getForces(), spring));
        p2.setForces(vecAdd(p2.getForces(), spring));
    }

    @Override
    public void draw() {
        glBegin( GL_LINES );
        glColor3d(0.6, 0.7, 0.8);
        glVertex2d(p1.getPosition().x, p1.getPosition().y);
        glColor3d(0.6, 0.7, 0.8);
        glVertex2d(p2.getPosition().x, p2.getPosition().y);
        glEnd();
    }

}
