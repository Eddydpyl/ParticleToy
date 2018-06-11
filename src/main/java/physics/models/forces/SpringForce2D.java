package physics.models.forces;

import physics.models.particles.FluidParticle2D;
import physics.models.particles.Particle2D;

import static org.lwjgl.opengl.GL11.*;
import static physics.LinearSolver.*;

public class SpringForce2D implements Force {

    private Particle2D p1, p2;
    private double ks, kd;
    private double distance;

    public SpringForce2D(Particle2D p1, Particle2D p2, double ks, double kd, double distance) {
        this.p1 = p1;
        this.p2 = p2;
        this.ks = ks;
        this.kd = kd;
        this.distance = distance;
    }

    @Override
    public void apply() {
        // Hook's Spring Law
        double[] posDiff = vecDiff(p1.getPosition(), p2.getPosition());
        double[] velDiff = vecDiff(p1.getVelocity(), p2.getVelocity());
        double module = vecModule(posDiff);
        double magnitude = ks * (module - distance) + kd * vecDot(posDiff, velDiff);
        double[] spring = vecTimesScalar(posDiff, magnitude);
        if (p1 instanceof FluidParticle2D) {
            FluidParticle2D fluidParticle = (FluidParticle2D) p1;
            fluidParticle.setForces(vecDiff(p1.getForces(), vecTimesScalar(spring, fluidParticle.getDensity())));
        } else p1.setForces(vecDiff(p1.getForces(), spring));
        if (p2 instanceof FluidParticle2D) {
            FluidParticle2D fluidParticle = (FluidParticle2D) p2;
            fluidParticle.setForces(vecAdd(p2.getForces(), vecTimesScalar(spring, fluidParticle.getDensity())));
        } else p2.setForces(vecAdd(p2.getForces(), spring));
    }

    @Override
    public void draw() {
        glBegin(GL_LINES);
        glColor3d(0.6, 0.7, 0.8);
        glVertex2d(p1.getPosition()[0], p1.getPosition()[1]);
        glColor3d(0.6, 0.7, 0.8);
        glVertex2d(p2.getPosition()[0], p2.getPosition()[1]);
        glEnd();
    }

}
