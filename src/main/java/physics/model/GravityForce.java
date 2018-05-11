package physics.model;

import com.sun.javafx.geom.Vec2d;

import static physics.LinearSolver2D.*;

public class GravityForce implements Force {

    private static final double G = 9.8;

    private Particle particle;

    public GravityForce(Particle particle) {
        this.particle = particle;
    }

    @Override
    public void apply() {
        Vec2d current = particle.getForces();
        Vec2d gravity = new Vec2d(0, - particle.getMass() * G);
        particle.setForces(vecAdd(current, gravity));
    }

    @Override
    public void draw() {}

}
