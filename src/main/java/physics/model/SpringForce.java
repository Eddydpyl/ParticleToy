package physics.model;

import com.sun.javafx.geom.Vec2d;

import static org.lwjgl.opengl.GL11.*;
import static physics.LinearSolver2D.*;

public class SpringForce implements Force {

    private Particle p1, p2;
    private double ks, kd;

    public SpringForce(Particle p1, Particle p2, double ks, double kd) {
        this.p1 = p1;
        this.p2 = p2;
        this.ks = ks;
        this.kd = kd;
    }

    @Override
    public void apply() {
        Vec2d spring = vecDiff(vecTimesScalar(vecDiff(p1.getPosition(), p2.getPosition()), ks), vecTimesScalar(vecDiff(p1.getVelocity(), p2.getVelocity()), kd));
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
